using Reactive.Bindings;

using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Views;

using XF.Material.Forms.UI.Dialogs;

namespace XamarinApp1.ViewModels;

[QueryProperty(nameof(Reload), nameof(Reload))]
public class ReportsViewModel : BaseViewModel
{
    public class ReportGroup : List<Report>
    {
        public ReportGroup(DateOnly date, IEnumerable<Report> source) : base(source)
        {
            Date = date;
            Name = date.ToString();
        }

        public DateOnly Date { get; private set; }

        public string Name { get; private set; }
    }

    public ReportsViewModel()
    {
        Refresh.Subscribe(() => LoadItems(true));

        NewReport.Subscribe(() => Shell.Current.GoToAsync($"{nameof(ReportDetailPage)}?NewAction=true"));

        ItemTapped.Subscribe(OnItemTapped);

        LoadItems(false);
    }

    public bool Reload
    {
        set
        {
            if (value)
            {
                LoadItems(true);
            }
        }
    }

    public ReactiveCommand Refresh { get; } = new();

    public ReactiveCommand NewReport { get; } = new();

    public ReactiveCommand<Report> ItemTapped { get; } = new();

    public ReactiveCollection<ReportGroup> Items { get; } = new();

    private async void OnItemTapped(Report item)
    {
        if (item != null)
        {
            if (await ReportDataStore.ExistsAsync(item.Id))
            {
                await Shell.Current.GoToAsync($"{nameof(ReportDetailPage)}?{nameof(ReportDetailViewModel.ItemId)}={item.Id}");
            }
            else
            {
                await MaterialDialog.Instance.SnackbarAsync("アイテムが存在しません");
            }
        }
    }

    private async void LoadItems(bool forceRefresh)
    {
        IsBusy = true;
        try
        {
            var items = (await ReportDataStore.GetItemsAsync(forceRefresh)).GroupBy(i => i.Date).OrderBy(i => i.Key).Select(i => new ReportGroup(i.Key, i));
            Items.Clear();
            foreach (var item in items)
            {
                Items.Add(item);
            }
        }
        finally
        {
            IsBusy = false;
        }
    }
}
