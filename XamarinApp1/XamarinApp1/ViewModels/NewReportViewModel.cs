using Reactive.Bindings;

using System;
using System.Collections.Generic;
using System.Linq;
using System.Reactive.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Services;

namespace XamarinApp1.ViewModels;

public class NewReportViewModel : BaseViewModel, IQueryAttributable
{
    private string _id;

    public NewReportViewModel()
    {
        var canSave = SelectedSubject.CombineLatest(Name).Select(t => t.First != null && !string.IsNullOrWhiteSpace(t.Second));
        SaveCommand = new ReactiveCommand(canSave);
        SaveCommand.Subscribe(async () =>
        {
            if (_id == null)
            {
                await ReportDataStore.AddItemAsync(new Report(
                    Subject: SelectedSubject.Value,
                    Date: DateOnly.FromDateTime(SelectedDate.Value),
                    Name: Name.Value,
                    Id: Guid.NewGuid(),
                    State: ReportState.NotSubmitted));
            }
            else
            {
                var report = await ReportDataStore.GetItemAsync(Guid.Parse(_id));
                await ReportDataStore.UpdateItemAsync(report with
                {
                    Subject = SelectedSubject.Value,
                    Date = DateOnly.FromDateTime(SelectedDate.Value),
                    Name = Name.Value,
                });
            }

            await Shell.Current.GoToAsync("..");
        });
        CancelCommand.Subscribe(() => Shell.Current.GoToAsync(".."));
        Task.Run(async () =>
        {
            IsBusy = true;

            try
            {
                Subjects.Value = (await DependencyService.Get<IDataStore<Subject>>().GetItemsAsync(true)).ToArray();
                SelectedSubject.Value = Subjects.Value.FirstOrDefault();
            }
            finally
            {
                IsBusy = false;
            }
        });
    }

    public ReactivePropertySlim<Subject> SelectedSubject { get; } = new();

    public ReactivePropertySlim<DateTime> SelectedDate { get; } = new(DateTime.Now);

    public ReactivePropertySlim<string> Name { get; } = new();

    public ReactivePropertySlim<Subject[]> Subjects { get; } = new();

    public ReactiveCommand SaveCommand { get; }

    public ReactiveCommand CancelCommand { get; } = new();

    public async void ApplyQueryAttributes(IDictionary<string, string> query)
    {
        if (query.TryGetValue("SelectedSubject", out var subject))
        {
            SelectedSubject.Value = await DependencyService.Get<IDataStore<Subject>>().GetItemAsync(Guid.Parse(subject));
        }
        
        if (query.TryGetValue("Id", out var id))
        {
            _id = id;
        }
        
        if (query.TryGetValue("SelectedDate", out var date))
        {
            SelectedDate.Value = DateOnly.Parse(Uri.UnescapeDataString(date)).ToDateTime(default);
        }
        
        if (query.TryGetValue("Name", out var name))
        {
            Name.Value = Uri.UnescapeDataString(name);
        }
    }
}
