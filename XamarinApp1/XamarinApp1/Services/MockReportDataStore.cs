using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;

namespace XamarinApp1.Services;

public class MockReportDataStore : IDataStore<Report>
{
    private readonly List<Report> _items = new()
    {
#if ENABLE_MOCK
        new Report(MockSubjectDataStore.AASubject, DateOnly.FromDateTime(DateTime.Now).AddDays(-5), "AAA", Guid.NewGuid(), ReportState.NotSubmitted),
        new Report(MockSubjectDataStore.AASubject, DateOnly.FromDateTime(DateTime.Now).AddDays(-4), "BBB", Guid.NewGuid(), ReportState.NotSubmitted),
        new Report(MockSubjectDataStore.AASubject, DateOnly.FromDateTime(DateTime.Now).AddDays(-3), "CCC", Guid.NewGuid(), ReportState.NotSubmitted),
        new Report(MockSubjectDataStore.AASubject, DateOnly.FromDateTime(DateTime.Now).AddDays(-2), "DDD", Guid.NewGuid(), ReportState.NotSubmitted),
        new Report(MockSubjectDataStore.AASubject, DateOnly.FromDateTime(DateTime.Now).AddDays(-1), "EEE", Guid.NewGuid(), ReportState.NotSubmitted),
        new Report(MockSubjectDataStore.MathSubject, DateOnly.FromDateTime(DateTime.Now), "第一回レポート", Guid.NewGuid(), ReportState.NotSubmitted),
        new Report(MockSubjectDataStore.JapaneseSubject, DateOnly.FromDateTime(DateTime.Now), "第一回レポート", Guid.NewGuid(), ReportState.Submitted),
        new Report(MockSubjectDataStore.AASubject, DateOnly.FromDateTime(DateTime.Now).AddDays(7), "第二回レポート", Guid.NewGuid(), ReportState.NotSubmitted),
        new Report(MockSubjectDataStore.JapaneseSubject, DateOnly.FromDateTime(DateTime.Now), "第二回レポート", Guid.NewGuid(), ReportState.NotSubmitted),
        new Report(MockSubjectDataStore.MathSubject, DateOnly.FromDateTime(DateTime.Now).AddDays(7), "第二回レポート", Guid.NewGuid(), ReportState.NotSubmitted),
#endif
    };
    private readonly IDataStore<Subject> _subjectStore = DependencyService.Get<IDataStore<Subject>>();

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

    public Task<bool> ExistsAsync(Guid id)
    {
        return Task.FromResult(_items.Select(i => i.Id).Contains(id));
    }

    public async Task<Report> GetItemAsync(Guid id)
    {
        var item = _items.FirstOrDefault(s => s.Id == id);
        item = item with
        {
            Subject = await _subjectStore.GetItemAsync(item.Subject.Id)
        };

        return await Task.FromResult(item);
    }

    public async Task<IEnumerable<Report>> GetItemsAsync(bool forceRefresh = false)
    {
        var list = new List<Report>();

        foreach (var task in _items.Select(async item =>
        {
            return item with
            {
                Subject = await _subjectStore.GetItemAsync(item.Subject.Id)
            };
        }))
        {
            list.Add(await task);
        }

        return list;
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
