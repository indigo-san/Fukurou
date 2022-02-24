using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

using Reactive.Bindings;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Views;

using static Android.Content.ClipData;

namespace XamarinApp1.ViewModels;

public class ReportsViewModel : BaseViewModel
{
    public class ReportGroup : List<Report>
    {
        public ReportGroup(DateOnly date, IEnumerable<Report> source) : base(source)
        {
            Name = date.ToString();
        }

        public string Name { get; private set; }
    }

    public ReportsViewModel()
    {
        Refresh.Subscribe(() => LoadItems(true));

        NewReport.Subscribe(() => Shell.Current.GoToAsync(nameof(NewReportPage)));

        ItemTapped.Subscribe(OnItemTapped);

        LoadItems(false);
    }

    public ReactiveCommand Refresh { get; } = new();

    public ReactiveCommand NewReport { get; } = new();

    public ReactiveCommand<Report> ItemTapped { get; } = new();

    public ReactiveCollection<ReportGroup> Items { get; } = new();

    private async void OnItemTapped(Report item)
    {
        if (item != null)
        {
            await Shell.Current.GoToAsync($"{nameof(ReportDetailPage)}?{nameof(ReportDetailViewModel.ItemId)}={item.Id}");
        }
    }

    private async void LoadItems(bool forceRefresh)
    {
        IsBusy = true;
        try
        {
            var items = (await ReportDataStore.GetItemsAsync(forceRefresh)).GroupBy(i => i.Date).OrderBy(i => i.Key).Select(i => new ReportGroup(i.Key, i));
            Items.Clear();
            Items.AddRangeOnScheduler(items);
        }
        finally
        {
            IsBusy = false;
        }
    }
}
