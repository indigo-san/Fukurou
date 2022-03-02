using Reactive.Bindings;

using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Views;

using XF.Material.Forms.UI.Dialogs;

namespace XamarinApp1.ViewModels;
public class SchoolDaysViewModel : BaseViewModel
{
    public record SchoolDayViewModel(SchoolDay Item)
    {
        public string DayOfWeek
            => CultureInfo.CurrentUICulture.DateTimeFormat.GetShortestDayName(Item.Date.DayOfWeek);

        public ReactivePropertySlim<bool> IsChecked { get; } = new();
    }

    public SchoolDaysViewModel()
    {
        Refresh.Subscribe(() => LoadItems(true));

        ItemTapped.Subscribe(OnItemTapped);

        SwitchIsEditing.Subscribe(OnSwitchIsEditing);

        Delete.Subscribe(OnDeleteItems);

        OnBackCommand.Subscribe(OnBack);

        LoadItems(false);
    }

    public ReactiveCommand Refresh { get; } = new();

    public ReactivePropertySlim<bool> IsEditing { get; } = new();

    public ReactiveCommand<SchoolDayViewModel> ItemTapped { get; } = new();

    public ReactiveCommand<SchoolDayViewModel> SwitchIsEditing { get; } = new();

    public ReactiveCommand OnBackCommand { get; } = new();

    public ReactiveCommand Delete { get; } = new();

    public ObservableCollection<SchoolDayViewModel> Items { get; } = new();

    public ValueTask RefreshTask { get; private set; }

    public async Task<string> NewSchoolDay(DateOnly date)
    {
        if (await SchoolDayDataStore.GetItemsAsync().FirstOrDefaultAsync(i => i.Date == date) is SchoolDay sd)
        {
            return $"{nameof(SchoolDayDetailPage)}?{nameof(SchoolDayDetailViewModel.ItemId)}={sd.Id}";
        }
        else
        {
            var item = new SchoolDay(date, Guid.NewGuid());
            await SchoolDayDataStore.AddItemAsync(item);
            return $"{nameof(SchoolDayDetailPage)}?{nameof(SchoolDayDetailViewModel.ItemId)}={item.Id}";
        }
    }

    private void OnBack()
    {
        if (IsEditing.Value)
        {
            IsEditing.Value = false;
            foreach (var item in Items)
            {
                item.IsChecked.Value = false;
            }
        }
        else
        {
            Shell.Current.GoToAsync("..");
        }
    }

    private void OnSwitchIsEditing(SchoolDayViewModel source)
    {
        IsEditing.Value = !IsEditing.Value;
        foreach (var item in Items)
        {
            item.IsChecked.Value = false;
        }

        source.IsChecked.Value = true;
    }

    private async void OnDeleteItems()
    {
        if (IsEditing.Value)
        {
            var oldItems = new List<(SchoolDayViewModel Item, int Index)>();
            for (int i = Items.Count - 1; i >= 0; i--)
            {
                SchoolDayViewModel item = Items[i];
                if (item.IsChecked.Value)
                {
                    Items.RemoveAt(i);
                    await SchoolDayDataStore.DeleteItemAsync(item.Item.Id);
                    oldItems.Add((item, i));
                }
            }

            IsEditing.Value = false;

            // 元に戻す
            if (await MaterialDialog.Instance.SnackbarAsync($"{oldItems.Count}個のアイテムが削除されました", "元に戻す"))
            {
                for (int i = oldItems.Count - 1; i >= 0; i--)
                {
                    var (item, index) = oldItems[i];

                    item.IsChecked.Value = false;
                    Items.Insert(index, item);
                    await SchoolDayDataStore.AddItemAsync(item.Item);
                }
            }
        }
    }

    private async void OnItemTapped(SchoolDayViewModel item)
    {
        if (item?.Item != null)
        {
            if (IsEditing.Value)
            {
                item.IsChecked.Value = !item.IsChecked.Value;
            }
            else if (await SchoolDayDataStore.ExistsAsync(item.Item.Id))
            {
                await Shell.Current.GoToAsync($"{nameof(SchoolDayDetailPage)}?{nameof(SchoolDayDetailViewModel.ItemId)}={item.Item.Id}");
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
        await RefreshTask;
        RefreshTask = new ValueTask(tcs.Task);
        try
        {
            var items = SchoolDayDataStore.GetItemsAsync(forceRefresh).OrderBy(i => i.Date);
            Items.Clear();
            await foreach (var item in items)
            {
                Items.Add(new SchoolDayViewModel(item));
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
