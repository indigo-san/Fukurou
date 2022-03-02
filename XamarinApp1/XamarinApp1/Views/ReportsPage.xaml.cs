using Android.App;

using Reactive.Bindings;

using System;
using System.Reactive.Linq;

using Xamarin.Forms;

using XamarinApp1.Services;
using XamarinApp1.ViewModels;
using XamarinApp1.Models;

using XF.Material.Forms.UI.Dialogs;
using XF.Material.Forms.UI.Dialogs.Configurations;

namespace XamarinApp1.Views;

public partial class ReportsPage : ContentPage
{
    private bool isFirstLoading = true;

    public ReportsPage()
    {
        InitializeComponent();
        SizeChanged += ReportsPage_SizeChanged;
    }

    private void ReportsPage_SizeChanged(object sender, EventArgs e)
    {
        if (isFirstLoading)
        {
            ScrollToToday_Clicked(null, EventArgs.Empty);
            isFirstLoading = false;
            SizeChanged -= ReportsPage_SizeChanged;
        }
    }

    private void ScrollToToday_Clicked(object sender, EventArgs e)
    {
        ScrollToDate(DateOnly.FromDateTime(DateTime.Now));
    }

    private async void ScrollToDate(DateOnly date)
    {
        if (BindingContext is not ReportsViewModel vm) return;

        await vm.RefreshTask;
        if (vm.Items.Count >= 1)
        {
            if (vm.Items[0].Date >= date)
            {
                CollectionView1.ScrollTo(0, 0, ScrollToPosition.Start);
            }
            else if (vm.Items[^1].Date <= date)
            {
                CollectionView1.ScrollTo(0, vm.Items.Count - 1, ScrollToPosition.Start);
            }
            else
            {
                for (int i = 0; i < vm.Items.Count - 1; i++)
                {
                    var current = vm.Items[i];
                    var next = vm.Items[i + 1];

                    if (current.Date <= date && date <= next.Date)
                    {
                        CollectionView1.ScrollTo(0, i + 1, ScrollToPosition.Start);
                        break;
                    }
                }
            }
        }
    }

    private void ScrollToSpecDate_Clicked(object sender, EventArgs e)
    {
        var now = DateTime.Now;
        var dialog = new DatePickerDialog(DependencyService.Get<Activity>(), (s, e) =>
        {
            ScrollToDate(DateOnly.FromDateTime(e.Date));
        }, now.Year, now.Month - 1, now.Day);
        dialog.Show();
    }

    private async void Filter_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is ReportsViewModel viewModel)
        {
            var dialogViewModel = new ReportFilterViewModel();
            await dialogViewModel.InitializeTask;
            var subjectDataStore = DependencyService.Get<IDataStore<Subject>>();

            if (Guid.TryParse(viewModel.FilterSubject, out var subjectId) &&
                (await subjectDataStore.GetItemAsync(subjectId)) is Subject subject)
            {
                dialogViewModel.IsSubjectEnabled.Value = true;
                dialogViewModel.SelectedSubject.Value = subject;
            }

            if (viewModel.FilterState.HasValue)
            {
                dialogViewModel.ReportState.Value = viewModel.FilterState.Value ? 0 : 1;
            }

            if (DateOnly.TryParse(viewModel.FilterMinDate, out DateOnly minDate))
            {
                dialogViewModel.IsMinDateEnabled.Value = true;
                dialogViewModel.MinDate.Value = minDate.ToDateTime(default);
            }

            if (DateOnly.TryParse(viewModel.FilterMaxDate, out DateOnly maxDate))
            {
                dialogViewModel.IsMaxDateEnabled.Value = true;
                dialogViewModel.MaxDate.Value = maxDate.ToDateTime(default);
            }

            var dialogView = new ReportFilterView()
            {
                BindingContext = dialogViewModel
            };

            if (await MaterialDialog.Instance.ShowCustomContentAsync(
                view: dialogView,
                message: "フィルターを指定") == true)
            {
                // フィルターを適用
                if (dialogViewModel.IsMinDateEnabled.Value)
                {
                    viewModel.FilterMinDate = DateOnly.FromDateTime(dialogViewModel.MinDate.Value).ToString();
                }
                else
                {
                    viewModel.FilterMinDate = null;
                }

                if (dialogViewModel.IsMaxDateEnabled.Value)
                {
                    viewModel.FilterMaxDate = DateOnly.FromDateTime(dialogViewModel.MaxDate.Value).ToString();
                }
                else
                {
                    viewModel.FilterMaxDate = null;
                }

                if (dialogViewModel.IsSubjectEnabled.Value)
                {
                    viewModel.FilterSubject = dialogViewModel.SelectedSubject.Value?.Id.ToString();
                }
                else
                {
                    viewModel.FilterSubject = null;
                }

                viewModel.FilterState = dialogViewModel.ReportState.Value switch
                {
                    1 => true,
                    2 => false,
                    _ => null
                };

                viewModel.Refresh.Execute();
            }
        }
    }
}
