using Reactive.Bindings;

using System;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Linq;
using System.Reactive.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Services;

using XF.Material.Forms.UI.Dialogs;

namespace XamarinApp1.ViewModels;

[QueryProperty(nameof(ItemId), nameof(ItemId))]
public class SubjectDetailViewModel : BaseViewModel
{
    private string itemId;

    public SubjectDetailViewModel()
    {
        Refresh.Subscribe(() => LoadItemId(itemId));
        Delete.Subscribe(OnDelete);
        Subject.Subscribe(sd => itemId = sd?.Id.ToString());
    }

    public string ItemId
    {
        get => itemId;
        set
        {
            itemId = value;
            LoadItemId(value);
        }
    }

    public ReactivePropertySlim<Subject> Subject { get; } = new();

    public ReactiveCommand Refresh { get; } = new();

    public ReactiveCommand Delete { get; } = new();

    public ReactivePropertySlim<float> SubmissionRate { get; } = new();

    public ReactivePropertySlim<float> ExpirationRate { get; } = new();

    public ReactivePropertySlim<string> SubmissionRateText { get; } = new();

    public ReactivePropertySlim<string> ExpirationRateText { get; } = new();

    public ReactivePropertySlim<int> SubmissionCount { get; } = new();

    public ReactivePropertySlim<int> NonSubmissionCount { get; } = new();

    public ReactivePropertySlim<int> ExpirationCount { get; } = new();

    public ReactivePropertySlim<int> ReportsCount { get; } = new();

    public ReactivePropertySlim<float> AttendanceRate { get; } = new();

    public ReactivePropertySlim<bool> IsVisiableRequiredAttendance { get; } = new();

    public ReactivePropertySlim<float> RequiredAttendanceRate { get; } = new();

    public ReactivePropertySlim<float> AbsenceRate { get; } = new();

    public ReactivePropertySlim<string> AttendanceRateText { get; } = new();

    public ReactivePropertySlim<string> AbsenceRateText { get; } = new();

    public ReactivePropertySlim<int> AttendanceCount { get; } = new();

    public ReactivePropertySlim<int> AbsenceCount { get; } = new();

    public ReactivePropertySlim<int> LessonsCount { get; } = new();

    public ValueTask RefreshTask { get; private set; }

    public async void UpdateRequiredATT(int num)
    {
        var value = Subject.Value with
        {
            RequiredAttendance = num
        };

        if (await SubjectDataStore.UpdateItemAsync(value))
        {
            Subject.Value = value;
            SetRequiredAttendance(LessonsCount.Value);
        }
    }

    public async void UpdateName(string name)
    {
        var value = Subject.Value with
        {
            SubjectName = name
        };

        if (await SubjectDataStore.UpdateItemAsync(value))
        {
            Subject.Value = value;
        }
    }

    public async void UpdateColor()
    {
        var value = Subject.Value with
        {
            Color = RandomColor.GetColor()
        };

        if (await SubjectDataStore.UpdateItemAsync(value))
        {
            Subject.Value = value;
        }
    }

    public async void LoadItemId(string itemId)
    {
        IsBusy = true;
        var tcs = new TaskCompletionSource();
        RefreshTask = new(tcs.Task);

        try
        {
            Subject.Value = await SubjectDataStore.GetItemAsync(Guid.Parse(itemId));

            // この教科のレポート
            var reports = await ReportDataStore.GetItemsAsync().Where(i => i.Subject.Id == Subject.Value.Id && !i.IsArchived)
                .ToArrayAsync();

            // 提出数、未提出数、期限切れ数、レポート数
            SubmissionCount.Value = reports.Count(i => i.IsSubmitted);
            NonSubmissionCount.Value = reports.Count(i => i.IsNotSubmitted);
            ExpirationCount.Value = reports.Count(i => i.IsExpirationOfTerm);
            ReportsCount.Value = reports.Length;

            // 提出率
            SubmissionRate.Value = reports.Length == 0
                ? 0 : (float)SubmissionCount.Value / reports.Length;
            SubmissionRateText.Value = SubmissionRate.Value.ToString("P1");

            // 期限切れ率
            ExpirationRate.Value = reports.Length == 0
                ? 0 : (float)ExpirationCount.Value / reports.Length;
            ExpirationRateText.Value = ExpirationRate.Value.ToString("P1");

            // この教科の授業
            var lessons = await LessonDataStore.GetItemsAsync().Where(i => i.Subject.Id == Subject.Value.Id && !i.IsArchived)
                .ToArrayAsync();

            // 出席数、欠席数、授業数
            AttendanceCount.Value = lessons.Count(i => i.State == LessonState.Attend);
            AbsenceCount.Value = lessons.Count(i => i.State == LessonState.Absent);
            LessonsCount.Value = lessons.Length;

            // 出席率
            AttendanceRate.Value = lessons.Length == 0
                ? 0 : (float)AttendanceCount.Value / lessons.Length;
            AttendanceRateText.Value = AttendanceRate.Value.ToString("P1");

            // 欠席率
            AbsenceRate.Value = lessons.Length == 0
                ? 0 : (float)AbsenceCount.Value / lessons.Length;
            AbsenceRateText.Value = AbsenceRate.Value.ToString("P1");

            // 必要な出席数
            SetRequiredAttendance(lessons.Length);

            tcs.SetResult();
        }
        catch (Exception ex)
        {
            tcs.SetException(ex);
            Debug.WriteLine("Failed to Load Item");
        }
        finally
        {
            IsBusy = false;
        }
    }

    private void SetRequiredAttendance(int lessonsCount)
    {
        // 必要な出席数
        if (Subject.Value.RequiredAttendance == -1 || lessonsCount == 0)
        {
            // 未指定
            RequiredAttendanceRate.Value = 0;
            IsVisiableRequiredAttendance.Value = false;
        }
        else if (Subject.Value.RequiredAttendance <= -2)
        {
            // 全て
            RequiredAttendanceRate.Value = lessonsCount;
            IsVisiableRequiredAttendance.Value = true;
        }
        else
        {
            RequiredAttendanceRate.Value = (float)Subject.Value.RequiredAttendance / lessonsCount;
            IsVisiableRequiredAttendance.Value = true;
        }
    }

    private async void OnDelete()
    {
        if (Subject.Value != null)
        {
            var subject = Subject.Value;
            var lessons = await LessonDataStore.GetItemsAsync().Where(i => i.Subject.Id == Subject.Value.Id).ToArrayAsync();
            foreach (var lesson in lessons)
            {
                await LessonDataStore.DeleteItemAsync(lesson.Id);
            }

            var reports = await ReportDataStore.GetItemsAsync().Where(i => i.Subject.Id == Subject.Value.Id).ToArrayAsync();
            foreach (var report in reports)
            {
                await ReportDataStore.DeleteItemAsync(report.Id);
            }

            await SubjectDataStore.DeleteItemAsync(Subject.Value.Id);

            await Shell.Current.GoToAsync("..");

            // 元に戻す
            if (await MaterialDialog.Instance.SnackbarAsync("アイテムが削除されました", "元に戻す"))
            {
                await SubjectDataStore.AddItemAsync(subject);

                foreach (var report in reports)
                {
                    await ReportDataStore.AddItemAsync(report);
                }

                foreach (var lesson in lessons)
                {
                    await LessonDataStore.AddItemAsync(lesson);
                }
            }
        }
    }
}
