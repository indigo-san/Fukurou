using Reactive.Bindings;

using System;
using System.Diagnostics;
using System.Reactive.Linq;

using Xamarin.Forms;

using XamarinApp1.Models;

namespace XamarinApp1.ViewModels;

[QueryProperty(nameof(ItemId), nameof(ItemId))]
public class LessonDetailViewModel : BaseViewModel
{
    private string itemId;

    public LessonDetailViewModel()
    {
        GoToEdit.Subscribe(() =>
        {
            //if (Report.Value != null)
            //{
            //    var date = Uri.EscapeDataString(Report.Value.Date.ToString());
            //    var name = Uri.EscapeDataString(Report.Value.Name);

            //    await Shell.Current.GoToAsync($"{nameof(NewReportPage)}?SelectedSubject={Report.Value.Subject?.Id ?? Guid.Empty}&Id={Report.Value.Id}&SelectedDate={date}&Name={name}");
            //}
        });

        Refresh.Subscribe(() => LoadItemId(itemId));

        IsAttendChecked = Lesson.Select(i => i?.State == LessonState.Attend).ToReactiveProperty();
        IsAbsentChecked = Lesson.Select(i => i?.State == LessonState.Absent).ToReactiveProperty();

        IsAttendChecked.Subscribe(v =>
        {
            if (v && Lesson.Value != null)
            {
                UpdateState(Lesson.Value, LessonState.Attend);
            }
        });
        IsAbsentChecked.Subscribe(v =>
        {
            if (v && Lesson.Value != null)
            {
                UpdateState(Lesson.Value, LessonState.Absent);
            }
        });
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

    public ReactiveCommand Refresh { get; } = new();

    public ReactiveCommand GoToEdit { get; } = new();

    public ReactivePropertySlim<Lesson> Lesson { get; } = new();

    public ReactiveProperty<bool> IsAttendChecked { get; }

    public ReactiveProperty<bool> IsAbsentChecked { get; }

    public async void UpdateState(Lesson lesson, LessonState state)
    {
        Lesson.Value = lesson = lesson with
        {
            State = state
        };
        await LessonDataStore.UpdateItemAsync(lesson);
    }

    public async void UpdateDate(Lesson lesson, DateOnly date)
    {
        Lesson.Value = lesson = lesson with
        {
            Date = date
        };
        await LessonDataStore.UpdateItemAsync(lesson);
    }

    public async void LoadItemId(string itemId)
    {
        IsBusy = true;
        try
        {
            Lesson.Value = await LessonDataStore.GetItemAsync(Guid.Parse(itemId));
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
