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
    private bool isLoading;

    public record ReportViewModel(Report Item)
    {
        public ReactivePropertySlim<bool> IsChecked { get; } = new();
    }

    public class ReportGroup : ObservableCollection<ReportViewModel>
    {
        public ReportGroup(DateOnly date, IEnumerable<ReportViewModel> source) : base(source)
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

        SwitchIsEditing.Subscribe(OnSwitchIsEditing);

        Delete.Subscribe(OnDeleteItems);

        OnBackCommand.Subscribe(OnBack);

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

    public ReactivePropertySlim<bool> IsEditing { get; } = new();

    public ReactiveCommand Refresh { get; } = new();

    public ReactiveCommand NewReport { get; } = new();

    public ReactiveCommand<ReportViewModel> ItemTapped { get; } = new();

    public ReactiveCommand<ReportViewModel> SwitchIsEditing { get; } = new();

    public ReactiveCommand OnBackCommand { get; } = new();

    public ReactiveCommand Delete { get; } = new();

    public ObservableCollection<ReportGroup> Items { get; } = new();

    public ValueTask RefreshTask { get; private set; }

    private void OnBack()
    {
        if (IsEditing.Value)
        {
            IsEditing.Value = false;
            foreach (var item in Items.SelectMany(i => i))
            {
                item.IsChecked.Value = false;
            }
        }
        else
        {
            Shell.Current.GoToAsync("..");
        }
    }

    private void OnSwitchIsEditing(ReportViewModel source)
    {
        IsEditing.Value = !IsEditing.Value;
        foreach (var item in Items.SelectMany(i => i))
        {
            item.IsChecked.Value = false;
        }

        source.IsChecked.Value = true;
    }

    private async void OnDeleteItems()
    {
        if (IsEditing.Value)
        {
            var oldGroups = new List<(bool IsEmpty, ReportGroup Group, int Index, List<ReportViewModel> Items)>();
            int count = 0;
            for (int i = Items.Count - 1; i >= 0; i--)
            {
                var oldItems = new List<ReportViewModel>();
                var group = Items[i];

                for (int ii = group.Count - 1; ii >= 0; ii--)
                {
                    var item = group[ii];

                    if (item.IsChecked.Value)
                    {
                        group.RemoveAt(ii);
                        await ReportDataStore.DeleteItemAsync(item.Item.Id);
                        oldItems.Add(item);
                        count++;
                    }
                }

                var isEmpty = group.Count <= 0;
                oldGroups.Add((isEmpty, group, i, oldItems));

                if (isEmpty)
                {
                    Items.RemoveAt(i);
                }
            }

            IsEditing.Value = false;

            // 元に戻す
            if (await MaterialDialog.Instance.SnackbarAsync($"{count}個のアイテムが削除されました", "元に戻す"))
            {
                for (int i = oldGroups.Count - 1; i >= 0; i--)
                {
                    var (isEmpty, group, index, items) = oldGroups[i];

                    if (isEmpty)
                    {
                        Items.Insert(index, group);
                    }

                    for (int ii = items.Count - 1; ii >= 0; ii--)
                    {
                        var item = items[ii];
                        item.IsChecked.Value = false;
                        group.Add(item);
                        await ReportDataStore.AddItemAsync(item.Item);
                    }
                }
            }
        }
    }

    private async void OnItemTapped(ReportViewModel item)
    {
        if (item != null)
        {
            if (IsEditing.Value)
            {
                item.IsChecked.Value = !item.IsChecked.Value;
            }
            else if (await ReportDataStore.ExistsAsync(item.Item.Id))
            {
                await Shell.Current.GoToAsync($"{nameof(ReportDetailPage)}?{nameof(ReportDetailViewModel.ItemId)}={item.Item.Id}");
            }
            else
            {
                await MaterialDialog.Instance.SnackbarAsync("アイテムが存在しません");
            }
        }
    }

    private async void LoadItems(bool forceRefresh)
    {
        if (isLoading) return;

        IsBusy = true;
        var tcs = new TaskCompletionSource();
        RefreshTask = new ValueTask(tcs.Task);
        try
        {
            isLoading = true;
            var items = ReportDataStore.GetItemsAsync(forceRefresh).GroupBy(i => i.Date).OrderBy(i => i.Key);
            Items.Clear();
            await foreach (var item in items)
            {
                Items.Add(new ReportGroup(item.Key, await item.Select(i => new ReportViewModel(i)).ToArrayAsync()));
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
            isLoading = false;
        }
    }
}
