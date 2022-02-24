using Android.Net.Wifi.Aware;

using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;

namespace XamarinApp1.Services;

public class MockSchoolDayDataStore : IDataStore<SchoolDay>
{
    private readonly List<SchoolDay> _items = new()
    {
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(-1)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(0)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(1)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(2)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(3)), Guid.NewGuid()),
        new SchoolDay(DateOnly.FromDateTime(DateTime.Now.AddDays(4)), Guid.NewGuid()),
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

    public async Task<SchoolDay> GetItemAsync(Guid id)
    {
        var item = _items.FirstOrDefault(s => s.Id == id);
        var lessons = (await _lessonStore.GetItemsAsync()).Where(i => i.Date == item.Date).ToArray();
        var reports = (await _reportStore.GetItemsAsync()).Where(i => i.Date == item.Date).ToArray();

        return await Task.FromResult(item with
        {
            Lessons = lessons,
            Reports = reports
        });
    }

    public async Task<IEnumerable<SchoolDay>> GetItemsAsync(bool forceRefresh = false)
    {
        var list = new List<SchoolDay>();

        foreach (var item in _items.Select(async item =>
        {
            var lessons = (await _lessonStore.GetItemsAsync(true)).Where(i => i.Date == item.Date).ToArray();
            var reports = (await _reportStore.GetItemsAsync(true)).Where(i => i.Date == item.Date).ToArray();
            return item with
            {
                Lessons = lessons,
                Reports = reports
            };
        }))
        {
            list.Add(await item);
        }

        return list;
    }
}
