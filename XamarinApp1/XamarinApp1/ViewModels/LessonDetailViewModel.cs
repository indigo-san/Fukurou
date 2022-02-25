using Java.Security.Cert;

using Reactive.Bindings;

using System;
using System.Diagnostics;
using System.Linq;
using System.Reactive.Linq;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;

using XF.Material.Forms.UI.Dialogs;

namespace XamarinApp1.ViewModels;

[QueryProperty(nameof(ItemId), nameof(ItemId))]
public class LessonDetailViewModel : BaseViewModel
{
    private string itemId;

    public LessonDetailViewModel()
    {
        static int StateToIndex(LessonState? state)
        {
            return state switch
            {
                LessonState.Attend => 0,
                LessonState.Absent => 1,
                _ => -1,
            };
        }
        Refresh.Subscribe(() => LoadItemId(itemId));

        Delete.Subscribe(async () =>
        {
            if (Lesson.Value != null)
            {
                var oldValue = Lesson.Value;
                await LessonDataStore.DeleteItemAsync(Lesson.Value.Id);

                await Shell.Current.GoToAsync("..");

                // 元に戻す
                if (await MaterialDialog.Instance.SnackbarAsync("アイテムが削除されました", "元に戻す"))
                {
                    await LessonDataStore.AddItemAsync(oldValue);
                }
            }
        });

        StateIndex = Lesson.Select(i => StateToIndex(i?.State)).ToReactiveProperty();

        StateIndex.Subscribe(idx =>
        {
            if (Lesson.Value != null && StateToIndex(Lesson.Value.State) != idx)
            {
                UpdateState(Lesson.Value, idx switch
                {
                    0 => LessonState.Attend,
                    1 => LessonState.Absent,
                    _ => LessonState.None,
                });
            }
        });

        Task.Run(async () =>
        {
            IsBusy = true;

            try
            {
                Subjects.Value = (await SubjectDataStore.GetItemsAsync(true)).ToArray();
            }
            finally
            {
                IsBusy = false;
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
    
    public ReactiveCommand Delete { get; } = new();

    public ReactivePropertySlim<Lesson> Lesson { get; } = new();

    public ReactivePropertySlim<Subject[]> Subjects { get; } = new();

    public ReactiveProperty<int> StateIndex { get; }

    public async void UpdateState(Lesson lesson, LessonState state)
    {
        Lesson.Value = lesson = lesson with
        {
            State = state
        };
        await LessonDataStore.UpdateItemAsync(lesson);
    }

    public async void UpdateSubject(Lesson lesson, Subject subject)
    {
        Lesson.Value = lesson = lesson with
        {
            Subject = subject
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

    public async void UpdateStart(Lesson lesson, TimeOnly start)
    {
        Lesson.Value = lesson = lesson with
        {
            Start = start
        };
        await LessonDataStore.UpdateItemAsync(lesson);
    }

    public async void UpdateEnd(Lesson lesson, TimeOnly end)
    {
        Lesson.Value = lesson = lesson with
        {
            End = end
        };
        await LessonDataStore.UpdateItemAsync(lesson);
    }

    public async void UpdateRoom(Lesson lesson, string room)
    {
        Lesson.Value = lesson = lesson with
        {
            Room = room
        };
        await LessonDataStore.UpdateItemAsync(lesson);
    }

    public async void UpdateMemo(Lesson lesson, string memo)
    {
        Lesson.Value = lesson = lesson with
        {
            Memo = memo
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
