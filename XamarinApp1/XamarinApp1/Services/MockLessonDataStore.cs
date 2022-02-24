
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

using XamarinApp1.Models;

namespace XamarinApp1.Services;

public class MockLessonDataStore : IDataStore<Lesson>
{
    private readonly List<Lesson> _items = new()
    {
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
    };

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

    public async Task<Lesson> GetItemAsync(Guid id)
    {
        return await Task.FromResult(_items.FirstOrDefault(s => s.Id == id));
    }

    public async Task<IEnumerable<Lesson>> GetItemsAsync(bool forceRefresh = false)
    {
        return await Task.FromResult(_items);
    }

    public async Task<bool> UpdateItemAsync(Lesson item)
    {
        var oldItem = _items.Where(arg => arg.Id == item.Id).FirstOrDefault();
        _items.Remove(oldItem);
        _items.Add(item);

        return await Task.FromResult(true);
    }
}
