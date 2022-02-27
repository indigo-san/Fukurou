using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

using XamarinApp1.Models;

namespace XamarinApp1.Services;

public class StorageSubjectDataStore : IDataStore<Subject>
{
    private readonly Task _initTask;
    private List<Subject> _items = new();

    public StorageSubjectDataStore()
    {
        _initTask = Task.Run(async () =>
        {
            _items = await Pull();
        });
    }

    public async Task<bool> AddItemAsync(Subject item)
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

    public async Task<Subject> GetItemAsync(Guid id)
    {
        await _initTask;
        return _items.FirstOrDefault(s => s.Id == id);
    }

    public async IAsyncEnumerable<Subject> GetItemsAsync(bool forceRefresh = false)
    {
        await _initTask;

        if (forceRefresh)
        {
            _items = await Pull();
        }

        foreach (var item in _items)
        {
            yield return item;
        }
    }

    public async Task<bool> UpdateItemAsync(Subject item)
    {
        await _initTask;
        var oldItem = _items.Where(arg => arg.Id == item.Id).FirstOrDefault();
        _items.Remove(oldItem);
        _items.Add(item);

        return await Push(_items);
    }

    private static async ValueTask<List<Subject>> Pull()
    {
        return await StorageHelper.Pull("subjects.json", () => new List<Subject>());
    }

    private static async ValueTask<bool> Push(List<Subject> data)
    {
        return await StorageHelper.Push("subjects.json", data);
    }
}
