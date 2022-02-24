using Reactive.Bindings;

using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Reactive.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Views;

namespace XamarinApp1.ViewModels;

[QueryProperty(nameof(ItemId), nameof(ItemId))]
public class SchoolDayDetailViewModel : BaseViewModel
{
    private string itemId;

    public SchoolDayDetailViewModel()
    {
        Refresh.Subscribe(() => LoadItemId(itemId));
        GoToReportDetail.Subscribe(async item =>
        {
            if (item != null)
            {
                await Shell.Current.GoToAsync($"{nameof(ReportDetailPage)}?{nameof(ReportDetailViewModel.ItemId)}={item.Id}");
            }
        });
        IsLessonsEmpty = SchoolDay.Select(i => i?.Lessons.Any() ?? true).ToReadOnlyReactivePropertySlim();
        IsReportsEmpty = SchoolDay.Select(i => i?.Reports.Any() ?? true).ToReadOnlyReactivePropertySlim();
    }

    public string ItemId
    {
        get => itemId;
        set
        {
            itemId = value;
            LoadItemId(value);
        }
    }

    public ReactivePropertySlim<SchoolDay> SchoolDay { get; } = new();

    public ReadOnlyReactivePropertySlim<bool> IsLessonsEmpty { get; }

    public ReadOnlyReactivePropertySlim<bool> IsReportsEmpty { get; }

    public ReactiveCommand<Report> GoToReportDetail { get; } = new();

    public ReactiveCommand Refresh { get; } = new();

    public async void UpdateDate(DateOnly date)
    {
        try
        {
            IsBusy = true;
            var item = SchoolDay.Value with
            {
                Date = date
            };
            await SchoolDayDataStore.UpdateItemAsync(item);
            SchoolDay.Value = item;
        }
        finally
        {
            IsBusy = false;
        }
    }

    public async void LoadItemId(string itemId)
    {
        IsBusy = true;
        try
        {
            SchoolDay.Value = await SchoolDayDataStore.GetItemAsync(Guid.Parse(itemId));
        }
        catch (Exception)
        {
            Debug.WriteLine("Failed to Load Item");
        }
        finally
        {
            IsBusy = false;
        }
    }
}
