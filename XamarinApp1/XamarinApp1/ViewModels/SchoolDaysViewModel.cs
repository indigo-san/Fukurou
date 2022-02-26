using Reactive.Bindings;

using System;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Views;

using XF.Material.Forms.UI.Dialogs;

namespace XamarinApp1.ViewModels;
public class SchoolDaysViewModel : BaseViewModel
{
    public SchoolDaysViewModel()
    {
        Refresh.Subscribe(() => LoadItems(true));

        ItemTapped.Subscribe(OnItemTapped);

        LoadItems(false);
    }

    public ReactiveCommand Refresh { get; } = new();

    public ReactiveCommand<SchoolDay> ItemTapped { get; } = new();

    public ObservableCollection<SchoolDay> Items { get; } = new();

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

    private async void OnItemTapped(SchoolDay item)
    {
        if (item != null)
        {
            if (await SchoolDayDataStore.ExistsAsync(item.Id))
            {
                await Shell.Current.GoToAsync($"{nameof(SchoolDayDetailPage)}?{nameof(SchoolDayDetailViewModel.ItemId)}={item.Id}");
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
            var items = SchoolDayDataStore.GetItemsAsync(forceRefresh).OrderBy(i => i.Date);
            Items.Clear();
            await foreach (var item in items)
            {
                Items.Add(item);
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
