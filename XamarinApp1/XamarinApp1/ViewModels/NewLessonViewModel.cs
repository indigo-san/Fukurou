
using Reactive.Bindings;

using System;
using System.Linq;
using System.Reactive.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Views;

namespace XamarinApp1.ViewModels;

[QueryProperty(nameof(DateRaw), "Date")]
public class NewLessonViewModel : BaseViewModel
{
    public NewLessonViewModel()
    {
        var canSave = SelectedSubject.CombineLatest(Start, End, Date).Select(items
            => items.First != null && items.Second <= items.Third && items.Fourth != null);
        Save = new ReactiveCommand(canSave);
        Save.Subscribe(async () =>
        {
            var item = new Lesson(SelectedSubject.Value, (DateOnly)Date.Value, TimeOnly.FromTimeSpan(Start.Value),
                                  TimeOnly.FromTimeSpan(End.Value), Guid.NewGuid());
            await LessonDataStore.AddItemAsync(item);

            await Shell.Current.GoToAsync("..");
            await Shell.Current.GoToAsync($"{nameof(LessonDetailPage)}?{nameof(LessonDetailViewModel.ItemId)}={item.Id}");
        });

        Task.Run(async () =>
        {
            IsBusy = true;

            try
            {
                Subjects.Value = (await SubjectDataStore.GetItemsAsync(true)).ToArray();
                SelectedSubject.Value ??= Subjects.Value.FirstOrDefault();
            }
            finally
            {
                IsBusy = false;
            }
        });
    }

    public string DateRaw
    {
        get => Date.Value?.ToString();
        set
        {
            var date = Uri.UnescapeDataString(value);
            if (DateOnly.TryParse(date, out var date2))
            {
                Date.Value = date2;
            }
        }
    }

    public ReactiveProperty<Subject> SelectedSubject { get; } = new();

    public ReactivePropertySlim<Subject[]> Subjects { get; } = new();

    public ReactivePropertySlim<TimeSpan> Start { get; } = new();

    public ReactivePropertySlim<TimeSpan> End { get; } = new();

    public ReactivePropertySlim<string> Room { get; } = new();

    public ReactivePropertySlim<string> Memo { get; } = new();

    public ReactivePropertySlim<DateOnly?> Date { get; } = new();

    public ReactiveCommand Save { get; }
}
