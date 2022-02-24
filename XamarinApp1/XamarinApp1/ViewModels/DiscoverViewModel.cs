using Reactive.Bindings;

using System;
using System.Linq;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Views;

namespace XamarinApp1.ViewModels;

public class DiscoverViewModel : BaseViewModel
{
    public DiscoverViewModel()
    {
        Refresh.Subscribe(async () =>
        {
            IsBusy = true;
            try
            {
                // NextSchoolDay
                var items = await SchoolDayDataStore.GetItemsAsync(true);
                var nextDay = items.FirstOrDefault(x => x.Date >= DateOnly.FromDateTime(DateTime.Now) && x.Lessons.Any(x => !x.IsCompleted));
                IsNextSchoolDayVisible.Value = nextDay != null;
                NextSchoolDay.Value = nextDay;

                // Reports
                var nowDateOnly = DateOnly.FromDateTime(DateTime.Now);
                var reports = (await ReportDataStore.GetItemsAsync(true)).Where(item => item.IsNotSubmitted).OrderBy(item => item.Date).Take(3);
                Reports.Clear();
                foreach (var item in reports)
                {
                    Reports.Add(item);
                }

                IsReportsEmpty.Value = !Reports.Any();
            }
            finally
            {
                IsBusy = false;
            }
        });

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

        Refresh.Execute();
    }

    public ReactiveCommand Refresh { get; } = new();

    public ReactiveCommand GoToSchoolDays { get; } = new();

    public ReactiveCommand GoToReports { get; } = new();

    public ReactiveCommand<SchoolDay> GoToSchoolDayDetail { get; } = new();

    public ReactiveCommand<Report> GoToReportDetail { get; } = new();

    public ReactiveProperty<bool> IsNextSchoolDayVisible { get; } = new();

    public ReactiveProperty<SchoolDay> NextSchoolDay { get; } = new();

    public ReactiveProperty<bool> IsReportsEmpty { get; } = new();

    public ReactiveCollection<Report> Reports { get; } = new();
}
