using Reactive.Bindings;

using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
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

        public string DayOfWeek
            => CultureInfo.CurrentUICulture.DateTimeFormat.GetShortestDayName(Date.DayOfWeek);

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

    public ObservableCollection<ReportGroup> Items { get; } = new();

    public ValueTask RefreshTask { get; private set; }

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
        var tcs = new TaskCompletionSource();
        RefreshTask = new ValueTask(tcs.Task);
        try
        {
            var items = ReportDataStore.GetItemsAsync(forceRefresh).GroupBy(i => i.Date).OrderBy(i => i.Key);
            Items.Clear();
            await foreach (var item in items)
            {
                Items.Add(new ReportGroup(item.Key, await item.ToArrayAsync()));
            }

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
