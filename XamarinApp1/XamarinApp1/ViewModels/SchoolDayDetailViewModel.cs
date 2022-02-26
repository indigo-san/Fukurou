using Reactive.Bindings;

using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Reactive.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Views;

using XF.Material.Forms.UI.Dialogs;

namespace XamarinApp1.ViewModels;

[QueryProperty(nameof(ItemId), nameof(ItemId))]
public class SchoolDayDetailViewModel : BaseViewModel
{
    private string itemId;

    public SchoolDayDetailViewModel()
    {
        Refresh.Subscribe(() => LoadItemId(itemId));

        Delete.Subscribe(async () =>
        {
            if (SchoolDay.Value != null)
            {
                var oldValue = SchoolDay.Value;
                foreach (var lesson in SchoolDay.Value.Lessons)
                {
                    await LessonDataStore.DeleteItemAsync(lesson.Id);
                }

                await SchoolDayDataStore.DeleteItemAsync(SchoolDay.Value.Id);

                await Shell.Current.GoToAsync("..");

                // 元に戻す
                if (await MaterialDialog.Instance.SnackbarAsync("アイテムが削除されました", "元に戻す"))
                {
                    foreach (var lesson in oldValue.Lessons)
                    {
                        await LessonDataStore.AddItemAsync(lesson);
                    }

                    await SchoolDayDataStore.AddItemAsync(oldValue);
                }
            }
        });

        GoToReportDetail.Subscribe(async item =>
        {
            if (item != null)
            {
                if (await ReportDataStore.ExistsAsync(item.Id))
                {
                    await Shell.Current.GoToAsync($"{nameof(ReportDetailPage)}?{nameof(ReportDetailViewModel.ItemId)}={item.Id}");
                }
                else
                {
                    await MaterialDialog.Instance.SnackbarAsync("アイテムが存在しません");
                }
            }
        });
        GoToLessonDetail.Subscribe(async item =>
        {
            if (item != null)
            {
                if (await LessonDataStore.ExistsAsync(item.Id))
                {
                    await Shell.Current.GoToAsync($"{nameof(LessonDetailPage)}?{nameof(LessonDetailViewModel.ItemId)}={item.Id}");
                }
                else
                {
                    await MaterialDialog.Instance.SnackbarAsync("アイテムが存在しません");
                }
            }
        });
        IsLessonsEmpty = SchoolDay.Select(i => i?.Lessons.Any() ?? true).ToReadOnlyReactivePropertySlim();
        IsReportsEmpty = SchoolDay.Select(i => i?.Reports.Any() ?? true).ToReadOnlyReactivePropertySlim();
        SchoolDay.Subscribe(sd => itemId = sd?.Id.ToString());
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

    public ReactivePropertySlim<SchoolDay> SchoolDay { get; } = new();

    public ReadOnlyReactivePropertySlim<bool> IsLessonsEmpty { get; }

    public ReadOnlyReactivePropertySlim<bool> IsReportsEmpty { get; }

    public ReactiveCommand<Report> GoToReportDetail { get; } = new();

    public ReactiveCommand<Lesson> GoToLessonDetail { get; } = new();

    public ReactiveCommand Refresh { get; } = new();

    public ReactiveCommand Delete { get; } = new();

    public async void UpdateDate(DateOnly date, bool updateReports)
    {
        var item = SchoolDay.Value with
        {
            Date = date
        };

        for (int i = 0; i < item.Lessons.Length; i++)
        {
            Lesson lesson = item.Lessons[i];
            item.Lessons[i] = lesson = lesson with
            {
                Date = date
            };

            await LessonDataStore.UpdateItemAsync(lesson);
        }

        if (updateReports)
        {
            for (int i = 0; i < item.Reports.Length; i++)
            {
                Report report = item.Reports[i];
                item.Reports[i] = report = report with
                {
                    Date = date
                };

                await ReportDataStore.UpdateItemAsync(report);
            }
        }

        if (await (SchoolDayDataStore.GetItemsAsync()).FirstOrDefaultAsync(i => i.Date == date) is SchoolDay item2)
        {
            // 既にdateに授業日が登録されている場合入れ替える
            await SchoolDayDataStore.DeleteItemAsync(item.Id);

            SchoolDay.Value = item2;
        }
        else
        {
            await SchoolDayDataStore.UpdateItemAsync(item);

            SchoolDay.Value = item;
        }
    }

    public async void LoadItemId(string itemId)
    {
        IsBusy = true;
        try
        {
            SchoolDay.Value = await SchoolDayDataStore.GetItemAsync(Guid.Parse(itemId));
        }
        catch (Exception)
        {
            Debug.WriteLine("Failed to Load Item");
        }
        finally
        {
            IsBusy = false;
        }
    }
}
