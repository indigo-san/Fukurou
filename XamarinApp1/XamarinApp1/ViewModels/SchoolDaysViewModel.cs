using System;
using System.Linq;

using Reactive.Bindings;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Views;

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

    private async void OnItemTapped(SchoolDay item)
    {
        if (item != null)
        {
            await Shell.Current.GoToAsync($"{nameof(SchoolDayDetailPage)}?{nameof(SchoolDayDetailViewModel.ItemId)}={item.Id}");
        }
    }

    private async void LoadItems(bool forceRefresh)
    {
        IsBusy = true;
        try
        {
            var items = (await SchoolDayDataStore.GetItemsAsync(forceRefresh)).OrderBy(i => i.Date);
            Items.Clear();
            Items.AddRangeOnScheduler(items);
        }
        finally
        {
            IsBusy = false;
        }
    }
}
