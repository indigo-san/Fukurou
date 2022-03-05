using Reactive.Bindings.Extensions;

using System;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.ViewModels;

using XF.Material.Forms.UI.Dialogs;

namespace XamarinApp1.Views;

public partial class SubjectDetailPage : ContentPage
{
    private IDisposable disposable1;
    private IDisposable disposable2;
    private IDisposable disposable3;
    private IDisposable disposable4;
    private IDisposable disposable5;

    public SubjectDetailPage()
    {
        InitializeComponent();
        SizeChanged += SubjectDetailPage_SizeChanged;
        ReportScoreExpander.ObserveProperty(o => o.IsExpanded).Subscribe(async value =>
        {
            if (value)
            {
                await ReportScoreChevron.RotateTo(180);
            }
            else
            {
                await ReportScoreChevron.RotateTo(0);
            }
        });
        LessonScoreExpander.ObserveProperty(o => o.IsExpanded).Subscribe(async value =>
        {
            if (value)
            {
                await LessonScoreChevron.RotateTo(180);
            }
            else
            {
                await LessonScoreChevron.RotateTo(0);
            }
        });
    }

    protected override void OnBindingContextChanged()
    {
        base.OnBindingContextChanged();
        if (BindingContext is SubjectDetailViewModel viewModel)
        {
            disposable1?.Dispose();
            disposable2?.Dispose();
            disposable3?.Dispose();
            disposable4?.Dispose();
            disposable5?.Dispose();

            disposable1 = viewModel.SubmissionRate.Subscribe(_ =>
            {
                Dispatcher.BeginInvokeOnMainThread(() =>
                {
                    UpdateReportRate();
                });
            });

            disposable2 = viewModel.ExpirationRate.Subscribe(_ =>
            {
                Dispatcher.BeginInvokeOnMainThread(() =>
                {
                    UpdateReportRate();
                });
            });

            disposable3 = viewModel.AttendanceRate.Subscribe(_ =>
            {
                Dispatcher.BeginInvokeOnMainThread(() =>
                {
                    UpdateLessonRate();
                });
            });

            disposable4 = viewModel.AbsenceRate.Subscribe(_ =>
            {
                Dispatcher.BeginInvokeOnMainThread(() =>
                {
                    UpdateLessonRate();
                });
            });

            disposable5 = viewModel.RequiredAttendanceRate.Subscribe(_ =>
            {
                Dispatcher.BeginInvokeOnMainThread(() =>
                {
                    UpdateRequiredAttendRate();
                });
            });
        }
    }

    private void SubjectDetailPage_SizeChanged(object sender, EventArgs e)
    {
        UpdateReportRate();
        UpdateLessonRate();
        UpdateRequiredAttendRate();
        SizeChanged -= SubjectDetailPage_SizeChanged;
    }

    private async void UpdateReportRate()
    {
        if (BindingContext is SubjectDetailViewModel viewModel)
        {
            await viewModel.RefreshTask;
            SubmissionRate.WidthRequest = SubmissionBase.Width * viewModel.SubmissionRate.Value;
            ExpirationRate.WidthRequest = SubmissionBase.Width * viewModel.ExpirationRate.Value;
            ExpirationRate.WidthRequest += SubmissionRate.WidthRequest;
        }
    }
    
    private async void UpdateLessonRate()
    {
        if (BindingContext is SubjectDetailViewModel viewModel)
        {
            await viewModel.RefreshTask;
            AttendanceRate.WidthRequest = AttendanceBase.Width * viewModel.AttendanceRate.Value;
            AbsenceRate.WidthRequest = AttendanceBase.Width * viewModel.AbsenceRate.Value;
            AbsenceRate.WidthRequest += AttendanceRate.WidthRequest;
        }
    }
    
    private async void UpdateRequiredAttendRate()
    {
        if (BindingContext is SubjectDetailViewModel viewModel)
        {
            await viewModel.RefreshTask;
            RequiredAttendBar.TranslationX = AttendanceBase.Width * viewModel.RequiredAttendanceRate.Value;
        }
    }

    private async void Rename_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is SubjectDetailViewModel viewModel)
        {
            await viewModel.RefreshTask;
            var name = await DisplayPromptAsync(
                "教科の名前を変更", "教科の名前を入力", initialValue: viewModel.Subject.Value.SubjectName);

            if (name != null)
            {
                viewModel.UpdateName(name);
            }
        }
    }

    private async void RandomColor_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is SubjectDetailViewModel viewModel)
        {
            await viewModel.RefreshTask;
            viewModel.UpdateColor();
        }
    }

    private void ReportScore_Clicked(object sender, EventArgs e)
    {
        ReportScoreExpander.IsExpanded = !ReportScoreExpander.IsExpanded;
    }

    private void LessonScore_Clicked(object sender, EventArgs e)
    {
        LessonScoreExpander.IsExpanded = !LessonScoreExpander.IsExpanded;
    }

    private void RequiredAttendanceEntry_Unfocused(object sender, FocusEventArgs e)
    {
        if (BindingContext is SubjectDetailViewModel vm &&
            int.TryParse(RequiredAttendanceEntry.Text, out int num))
        {
            vm.UpdateRequiredATT(num);
        }
    }
}
