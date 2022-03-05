using Reactive.Bindings;

using System;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Views;

namespace XamarinApp1.ViewModels;

public class DiscoverViewModel : BaseViewModel
{
    public DiscoverViewModel()
    {
        Refresh.Subscribe(() => LoadItems(true));

        GoToSchoolDays.Subscribe(async () =>
        {
            await Shell.Current.GoToAsync(nameof(SchoolDaysPage));
        });

        GoToSchoolDayDetail.Subscribe(async item =>
        {
            if (item != null && await SchoolDayDataStore.ExistsAsync(item.Id))
            {
                await Shell.Current.GoToAsync($"{nameof(SchoolDayDetailPage)}?{nameof(SchoolDayDetailViewModel.ItemId)}={item.Id}");
            }
        });

        GoToReports.Subscribe(async () =>
        {
            await Shell.Current.GoToAsync(nameof(ReportsPage));
        });

        GoToReportDetail.Subscribe(async item =>
        {
            if (item != null && await ReportDataStore.ExistsAsync(item.Id))
            {
                await Shell.Current.GoToAsync($"{nameof(ReportDetailPage)}?{nameof(ReportDetailViewModel.ItemId)}={item.Id}");
            }
        });

        LoadItems(false);
    }

    public ReactiveCommand Refresh { get; } = new();

    public ReactiveCommand GoToSchoolDays { get; } = new();

    public ReactiveCommand GoToReports { get; } = new();

    public ReactiveCommand<SchoolDay> GoToSchoolDayDetail { get; } = new();

    public ReactiveCommand<Report> GoToReportDetail { get; } = new();

    public ReactiveProperty<bool> IsNextSchoolDayVisible { get; } = new();

    public ReactiveProperty<SchoolDay> NextSchoolDay { get; } = new();

    public ReactiveProperty<bool> IsReportsEmpty { get; } = new();

    public ObservableCollection<Report> Reports { get; } = new();

    public ValueTask RefreshTask { get; private set; }

    private async void LoadItems(bool forceRefresh)
    {
        IsBusy = true;
        var tcs = new TaskCompletionSource();
        await RefreshTask;
        RefreshTask = new ValueTask(tcs.Task);
        try
        {
            // NextSchoolDay
            var items = SchoolDayDataStore.GetItemsAsync(forceRefresh);
            var nextDay = await items.FirstOrDefaultAsync(x => x.Date >= DateOnly.FromDateTime(DateTime.Now) && x.Lessons.Any(x => !x.IsCompleted));
            IsNextSchoolDayVisible.Value = nextDay != null;
            NextSchoolDay.Value = nextDay;

            // Reports
            var nowDateOnly = DateOnly.FromDateTime(DateTime.Now);
            var reports = ReportDataStore.GetItemsAsync(forceRefresh).Where(item => item.IsNotSubmitted && !item.IsArchived).OrderBy(item => item.Date).Take(3);
            Reports.Clear();
            await foreach (var item in reports)
            {
                Reports.Add(item);
            }

            IsReportsEmpty.Value = !Reports.Any();

            tcs.SetResult();
        }
        catch (Exception ex)
        {
            tcs.SetException(ex);
        }
        finally
        {
            IsBusy = false;
        }
    }
}
