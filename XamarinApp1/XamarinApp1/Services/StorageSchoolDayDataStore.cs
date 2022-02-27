
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reactive.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;

namespace XamarinApp1.Services;

public class StorageSchoolDayDataStore : IDataStore<SchoolDay>
{
    private readonly Task _initTask;
    private readonly IDataStore<Lesson> _lessonStore = DependencyService.Get<IDataStore<Lesson>>();
    private readonly IDataStore<Report> _reportStore = DependencyService.Get<IDataStore<Report>>();
    private List<SchoolDay> _items = new();

    public StorageSchoolDayDataStore()
    {
        _initTask = Task.Run(async () =>
        {
            _items = await Pull();
        });
    }

    public async Task<bool> AddItemAsync(SchoolDay item)
    {
        await _initTask;
        _items.Add(item);

        return await Push(_items);
    }

    public async Task<bool> UpdateItemAsync(SchoolDay item)
    {
        await _initTask;
        var oldItem = _items.Where(arg => arg.Id == item.Id).FirstOrDefault();
        _items.Remove(oldItem);
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

    public async Task<SchoolDay> GetItemAsync(Guid id)
    {
        await _initTask;
        var item = _items.FirstOrDefault(s => s.Id == id);
        var lessons = _lessonStore.GetItemsAsync().Where(i => i.Date == item.Date).OrderBy(i => i.Start).ToArrayAsync();
        var reports = _reportStore.GetItemsAsync().Where(i => i.Date == item.Date).ToArrayAsync();

        return item with
        {
            Lessons = await lessons,
            Reports = await reports
        };
    }

    public async IAsyncEnumerable<SchoolDay> GetItemsAsync(bool forceRefresh = false)
    {
        await _initTask;

        if (forceRefresh)
        {
            _items = await Pull();
        }

        foreach (var item in _items.Select(async item =>
        {
            var lessons = _lessonStore.GetItemsAsync(true).Where(i => i.Date == item.Date).OrderBy(i => i.Start).ToArrayAsync();
            var reports = _reportStore.GetItemsAsync(true).Where(i => i.Date == item.Date).ToArrayAsync();
            return item with
            {
                Lessons = await lessons,
                Reports = await reports
            };
        }))
        {
            yield return await item;
        }
    }

    private static async ValueTask<List<SchoolDay>> Pull()
    {
        return await StorageHelper.Pull("schoolDays.json", () => new List<SchoolDay>());
    }

    private static async ValueTask<bool> Push(List<SchoolDay> data)
    {
        return await StorageHelper.Push("schoolDays.json", data);
    }
}
