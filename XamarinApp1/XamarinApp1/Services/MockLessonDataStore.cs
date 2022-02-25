
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;

namespace XamarinApp1.Services;

public class MockLessonDataStore : IDataStore<Lesson>
{
    private readonly List<Lesson> _items = new()
    {
#if ENABLE_MOCK
        new Lesson(MockSubjectDataStore.AASubject,
                    DateOnly.FromDateTime(DateTime.Now), new TimeOnly(9, 0), new TimeOnly(9, 50),
                    Guid.NewGuid()),
        new Lesson(MockSubjectDataStore.BBSubject,
                    DateOnly.FromDateTime(DateTime.Now), new TimeOnly(10, 0), new TimeOnly(10, 50),
                    Guid.NewGuid()),
        new Lesson(MockSubjectDataStore.JapaneseSubject,
                    DateOnly.FromDateTime(DateTime.Now.AddDays(1)), new TimeOnly(9, 0), new TimeOnly(9, 50),
                    Guid.NewGuid()),
        new Lesson(MockSubjectDataStore.MathSubject,
                    DateOnly.FromDateTime(DateTime.Now.AddDays(1)), new TimeOnly(10, 0), new TimeOnly(10, 50),
                    Guid.NewGuid())
#endif
    };
    private readonly IDataStore<Subject> _subjectStore = DependencyService.Get<IDataStore<Subject>>();

    public async Task<bool> AddItemAsync(Lesson item)
    {
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

    public async Task<Lesson> GetItemAsync(Guid id)
    {
        var item = _items.FirstOrDefault(s => s.Id == id);
        item = item with
        {
            Subject = await _subjectStore.GetItemAsync(item.Subject.Id)
        };

        return await Task.FromResult(item);
    }

    public async Task<IEnumerable<Lesson>> GetItemsAsync(bool forceRefresh = false)
    {
        var list = new List<Lesson>();

        foreach (var item in _items.Select(async item =>
        {
            return item with
            {
                Subject = await _subjectStore.GetItemAsync(item.Subject.Id)
            };
        }))
        {
            list.Add(await item);
        }

        return list;
    }

    public async Task<bool> UpdateItemAsync(Lesson item)
    {
        var oldItem = _items.Where(arg => arg.Id == item.Id).FirstOrDefault();
        _items.Remove(oldItem);
        _items.Add(item);

        return await Task.FromResult(true);
    }
}
