using System;
using System.Linq;
using System.Threading.Tasks;

using Reactive.Bindings;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Views;

using XF.Material.Forms.UI.Dialogs;

using static Android.Content.ClipData;

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

    public ReactiveCollection<SchoolDay> Items { get; } = new();

    public async Task<string> NewSchoolDay(DateOnly date)
    {
        if ((await SchoolDayDataStore.GetItemsAsync()).FirstOrDefault(i => i.Date == date) is SchoolDay sd)
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
        try
        {
            var items = (await SchoolDayDataStore.GetItemsAsync(forceRefresh)).OrderBy(i => i.Date);
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
