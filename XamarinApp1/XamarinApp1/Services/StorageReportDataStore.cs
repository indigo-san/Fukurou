using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;

namespace XamarinApp1.Services;

public class StorageReportDataStore : IDataStore<Report>
{
    private readonly Task _initTask;
    private readonly IDataStore<Subject> _subjectStore = DependencyService.Get<IDataStore<Subject>>();
    private List<Report> _items = new();

    public StorageReportDataStore()
    {
        _initTask = Task.Run(async () =>
        {
            _items = await Pull();
        });
    }

    public async Task<bool> AddItemAsync(Report item)
    {
        await _initTask;
        _items.Add(item);

        return await Push(_items);
    }

    public async Task<bool> DeleteItemAsync(Guid id)
    {
        await _initTask;
        var oldItem = _items.Where(arg => arg.Id == id).FirstOrDefault();
        _items.Remove(oldItem);

        return await Push(_items);
    }

    public async Task<bool> ExistsAsync(Guid id)
    {
        await _initTask;
        return _items.Select(i => i.Id).Contains(id);
    }

    public async Task<Report> GetItemAsync(Guid id)
    {
        await _initTask;
        var item = _items.FirstOrDefault(s => s.Id == id);
        return item with
        {
            Subject = await _subjectStore.GetItemAsync(item.Subject.Id)
        };
    }

    public async IAsyncEnumerable<Report> GetItemsAsync(bool forceRefresh = false)
    {
        await _initTask;
        if (forceRefresh)
        {
            _items = await Pull();
        }

        foreach (var item in _items.Select(async item =>
        {
            return item with
            {
                Subject = await _subjectStore.GetItemAsync(item.Subject.Id)
            };
        }))
        {
            yield return await item;
        }
    }

    public async Task<bool> UpdateItemAsync(Report item)
    {
        await _initTask;
        var oldItem = _items.Where(arg => arg.Id == item.Id).FirstOrDefault();
        var index = _items.IndexOf(oldItem);
        _items.RemoveAt(index);
        _items.Insert(index, item);

        return await Push(_items);
    }

    private static async ValueTask<List<Report>> Pull()
    {
        return await StorageHelper.Pull("reports.json", () => new List<Report>());
    }

    private static async ValueTask<bool> Push(List<Report> data)
    {
        return await StorageHelper.Push("reports.json", data);
    }
}
