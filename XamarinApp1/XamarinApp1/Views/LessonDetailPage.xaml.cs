using Android.App;

using System;
using System.Linq;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using XamarinApp1.Models;
using XamarinApp1.ViewModels;

using XF.Material.Forms.UI.Dialogs;

namespace XamarinApp1.Views;

public partial class LessonDetailPage : ContentPage
{
    public LessonDetailPage()
    {
        InitializeComponent();
        StateButtons.Choices = new string[]
        {
            "出席",
            "欠席",
        };
    }

    private void ChangeDate_Tapped(object sender, EventArgs e)
    {
        if (BindingContext is LessonDetailViewModel vm && vm.Lesson.Value is Lesson lesson)
        {
            var dialog = new DatePickerDialog(DependencyService.Get<Activity>(), (s, e) =>
            {
                vm.UpdateDate(lesson, DateOnly.FromDateTime(e.Date));
            }, lesson.Date.Year, lesson.Date.Month - 1, lesson.Date.Day);
            dialog.Show();
        }
    }

    private async void Subject_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is LessonDetailViewModel vm &&
            vm.Subjects.Value is Subject[] subjects &&
            vm.Lesson.Value is Lesson lesson)
        {
            var index = await MaterialDialog.Instance.SelectChoiceAsync("教科を変更", subjects.Select(i => i.SubjectName).ToArray());

            if (0 <= index && index < subjects.Length)
            {
                var subject = subjects[index];
                vm.UpdateSubject(lesson, subject);
            }
        }
    }

    private TimeOnly start;
    private TimeOnly end;

    private void ChangeTime_Tapped(object sender, EventArgs e)
    {
        if (BindingContext is LessonDetailViewModel vm && vm.Lesson.Value is not null)
        {
            start = vm.Lesson.Value.Start;
            end = vm.Lesson.Value.End;
            ChangeStartTime_Tapped(vm);
        }
    }

    private void ChangeStartTime_Tapped(LessonDetailViewModel vm)
    {
        var dialog = new TimePickerDialog(DependencyService.Get<Activity>(), (s, e) =>
        {
            start = new TimeOnly(e.HourOfDay, e.Minute);
            ChangeEndTime_Tapped(vm);
        }, vm.Lesson.Value.Start.Hour, vm.Lesson.Value.Start.Minute, true);

        dialog.SetTitle("開始時間を設定");
        dialog.Show();
    }

    private void ChangeEndTime_Tapped(LessonDetailViewModel vm)
    {
        var dialog = new TimePickerDialog(DependencyService.Get<Activity>(), async (s, e) =>
        {
            end = new TimeOnly(e.HourOfDay, e.Minute);
            if (start > end)
            {
                await MaterialDialog.Instance.SnackbarAsync("終了時間を開始時間より前に設定できません");
            }
            else
            {
                vm.UpdateStart(vm.Lesson.Value, start);
                vm.UpdateEnd(vm.Lesson.Value, end);
            }
        }, vm.Lesson.Value.End.Hour, vm.Lesson.Value.End.Minute, true);

        dialog.SetTitle("終了時間を設定");
        dialog.Show();
    }

    private void RoomEntry_Unfocused(object sender, FocusEventArgs e)
    {
        if (BindingContext is LessonDetailViewModel vm &&
            vm.Lesson.Value is Lesson lesson &&
            RoomEntry.Text != lesson.Room)
        {
            vm.UpdateRoom(lesson, RoomEntry.Text);
        }
    }

    private void MemoEditor_Unfocused(object sender, FocusEventArgs e)
    {
        if (BindingContext is LessonDetailViewModel vm &&
            vm.Lesson.Value is Lesson lesson &&
            MemoEditor.Text != lesson.Memo)
        {
            vm.UpdateMemo(lesson, MemoEditor.Text);
        }
    }
}
