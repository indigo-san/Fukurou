using System;
using System.Collections.ObjectModel;
using System.Linq;

using Reactive.Bindings;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Services;
using XamarinApp1.Views;

namespace XamarinApp1.ViewModels;

public class SubjectsViewModel : BaseViewModel
{
    public SubjectsViewModel()
    {
        Refresh.Subscribe(() => LoadItems(true));

        NewSubject.Subscribe(async item =>
        {
            await SubjectDataStore.AddItemAsync(item);
            LoadItems(false);
        });
        
        ItemTapped.Subscribe(async item => await Shell.Current.GoToAsync($"SubjectDetailPage?ItemId={item.Id}"));

        LoadItems(false);
    }

    public ReactiveCommand Refresh { get; } = new();

    public ReactiveCommand<Subject> NewSubject { get; } = new();
    
    public ReactiveCommand<Subject> ItemTapped { get; } = new();

    public ObservableCollection<Subject> Items { get; } = new();

    private async void LoadItems(bool forceRefresh)
    {
        IsBusy = true;
        try
        {
            var items = (DependencyService.Get<IDataStore<Subject>>().GetItemsAsync(forceRefresh)).OrderBy(i => i.SubjectName);
            Items.Clear();
            await foreach (var item in items)
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
