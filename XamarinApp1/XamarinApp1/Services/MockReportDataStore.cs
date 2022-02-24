using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

using XamarinApp1.Models;

namespace XamarinApp1.Services;

public class MockReportDataStore : IDataStore<Report>
{
    private readonly List<Report> _items = new()
    {
        new Report(MockSubjectDataStore.AASubject, DateOnly.FromDateTime(DateTime.Now), "第一回レポート", Guid.NewGuid(), ReportState.ExpirationOfTerm),
        new Report(MockSubjectDataStore.MathSubject, DateOnly.FromDateTime(DateTime.Now), "第一回レポート", Guid.NewGuid(), ReportState.NotSubmitted),
        new Report(MockSubjectDataStore.JapaneseSubject, DateOnly.FromDateTime(DateTime.Now), "第一回レポート", Guid.NewGuid(), ReportState.Submitted),
        new Report(MockSubjectDataStore.AASubject, DateOnly.FromDateTime(DateTime.Now).AddDays(7), "第二回レポート", Guid.NewGuid(), ReportState.NotSubmitted),
        new Report(MockSubjectDataStore.JapaneseSubject, DateOnly.FromDateTime(DateTime.Now), "第二回レポート", Guid.NewGuid(), ReportState.NotSubmitted),
        new Report(MockSubjectDataStore.MathSubject, DateOnly.FromDateTime(DateTime.Now).AddDays(7), "第二回レポート", Guid.NewGuid(), ReportState.NotSubmitted),
    };

    public async Task<bool> AddItemAsync(Report item)
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

    public async Task<Report> GetItemAsync(Guid id)
    {
        return await Task.FromResult(_items.FirstOrDefault(s => s.Id == id));
    }

    public async Task<IEnumerable<Report>> GetItemsAsync(bool forceRefresh = false)
    {
        return await Task.FromResult(_items);
    }

    public async Task<bool> UpdateItemAsync(Report item)
    {
        var oldItem = _items.Where(arg => arg.Id == item.Id).FirstOrDefault();
        var index = _items.IndexOf(oldItem);
        _items.RemoveAt(index);
        _items.Insert(index, item);

        return await Task.FromResult(true);
    }
}
