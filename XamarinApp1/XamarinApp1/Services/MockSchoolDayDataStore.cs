using Android.Net.Wifi.Aware;

using System;
using System.Collections.Generic;
using System.Linq;
using System.Reactive;
using System.Reactive.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;

namespace XamarinApp1.Services;

public class MockSchoolDayDataStore : IDataStore<SchoolDay>
{
    private readonly List<SchoolDay> _items = new()
    {
#if ENABLE_MOCK
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(-4)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(-3)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(-2)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(-1)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(0)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(1)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(2)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(3)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(4)), Guid.NewGuid()),
#endif
    };
    private readonly IDataStore<Lesson> _lessonStore = DependencyService.Get<IDataStore<Lesson>>();
    private readonly IDataStore<Report> _reportStore = DependencyService.Get<IDataStore<Report>>();

    public MockSchoolDayDataStore()
    {
    }

    public async Task<bool> AddItemAsync(SchoolDay item)
    {
        _items.Add(item);

        return await Task.FromResult(true);
    }

    public async Task<bool> UpdateItemAsync(SchoolDay item)
    {
        var oldItem = _items.Where(arg => arg.Id == item.Id).FirstOrDefault();
        _items.Remove(oldItem);
        _items.Add(item);

        return await Task.FromResult(true);
    }

    public async Task<bool> DeleteItemAsync(Guid id)
    {
        var oldItem = _items.Where(arg => arg.Id == id).FirstOrDefault();
        _items.Remove(oldItem);

        return await Task.FromResult(true);
    }

    public Task<bool> ExistsAsync(Guid id)
    {
        return Task.FromResult(_items.Select(i => i.Id).Contains(id));
    }

    public async Task<SchoolDay> GetItemAsync(Guid id)
    {
        var item = _items.FirstOrDefault(s => s.Id == id);
        var lessons = _lessonStore.GetItemsAsync().Where(i => i.Date == item.Date).OrderBy(i => i.Start).ToArrayAsync();
        var reports = _reportStore.GetItemsAsync().Where(i => i.Date == item.Date).ToArrayAsync();

        return await Task.FromResult(item with
        {
            Lessons = await lessons,
            Reports = await reports
        });
    }

    public async IAsyncEnumerable<SchoolDay> GetItemsAsync(bool forceRefresh = false)
    {
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
}
