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

    //public ObservableCollection<Report> Reports { get; } = new();

    public ValueTask RefreshTask { get; private set; }

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
