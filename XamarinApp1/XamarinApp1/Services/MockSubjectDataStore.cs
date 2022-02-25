using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

using XamarinApp1.Models;

using Xamarin.Forms;

namespace XamarinApp1.Services;

public class MockSubjectDataStore : IDataStore<Subject>
{
    private readonly List<Subject> _items = new()
    {
#if ENABLE_MOCK
        AASubject,
        BBSubject,
        JapaneseSubject,
        MathSubject
#endif
    };
#if ENABLE_MOCK
    public static readonly Subject AASubject = new("AA", Guid.NewGuid(), Color.FromUint(0xff00e676));
    public static readonly Subject BBSubject = new("BB", Guid.NewGuid(), Color.FromUint(0xff00e676));
    public static readonly Subject JapaneseSubject = new("国語", Guid.NewGuid(), Color.FromUint(0xffff1744));
    public static readonly Subject MathSubject = new("数学", Guid.NewGuid(), Color.FromUint(0xff2979ff));
#endif

    public async Task<bool> AddItemAsync(Subject item)
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

    public async Task<Subject> GetItemAsync(Guid id)
    {
        return await Task.FromResult(_items.FirstOrDefault(s => s.Id == id));
    }

    public async Task<IEnumerable<Subject>> GetItemsAsync(bool forceRefresh = false)
    {
        return await Task.FromResult(_items);
    }

    public async Task<bool> UpdateItemAsync(Subject item)
    {
        var oldItem = _items.Where(arg => arg.Id == item.Id).FirstOrDefault();
        _items.Remove(oldItem);
        _items.Add(item);

        return await Task.FromResult(true);
    }
}
