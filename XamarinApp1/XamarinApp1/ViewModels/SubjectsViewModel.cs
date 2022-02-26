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

        UpdateColor.Subscribe(async item =>
        {
            var index = Items.IndexOf(item);
            item = item with
            {
                Color = RandomColor.GetColor()
            };

            Items[index] = item;
            await DependencyService.Get<IDataStore<Subject>>().UpdateItemAsync(item);
        });

        UpdateName.Subscribe(async item =>
        {
            var index = Items.IndexOf(item.Item1);
            var item1 = item.Item1 with
            {
                SubjectName = item.Item2
            };

            Items[index] = item1;
            await DependencyService.Get<IDataStore<Subject>>().UpdateItemAsync(item1);
        });

        NewSubject.Subscribe(async item =>
        {
            await DependencyService.Get<IDataStore<Subject>>().AddItemAsync(item);
            LoadItems(false);
        });
        
        Delete.Subscribe(async item =>
        {
            Items.Remove(item);
            await DependencyService.Get<IDataStore<Subject>>().DeleteItemAsync(item.Id);
        });

        LoadItems(false);
    }

    public ReactiveCommand Refresh { get; } = new();

    public ReactiveCommand<Subject> NewSubject { get; } = new();

    public ReactiveCommand<Subject> UpdateColor { get; } = new();
    
    public ReactiveCommand<Subject> Delete { get; } = new();

    public ReactiveCommand<(Subject, string)> UpdateName { get; } = new();

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
