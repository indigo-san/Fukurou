using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Shapes;
using Xamarin.Forms.Xaml;

using XamarinApp1.Models;
using XamarinApp1.ViewModels;

namespace XamarinApp1.Views
{
    public partial class DiscoverPage : ContentPage
    {
        private IDisposable _disposable;

        public DiscoverPage()
        {
            InitializeComponent();
        }

        protected override void OnBindingContextChanged()
        {
            base.OnBindingContextChanged();
            _disposable?.Dispose();
            if (BindingContext is DiscoverViewModel vm)
            {
                _disposable = vm.NextSchoolDay.Subscribe(d =>
                {
                    if (Dispatcher.IsInvokeRequired)
                    {
                        Dispatcher.BeginInvokeOnMainThread(() => UpdateLessonsIcons(d));
                    }
                    else
                    {
                        UpdateLessonsIcons(d);
                    }
                });
            }
        }

        private void UpdateLessonsIcons(SchoolDay day)
        {
            LessonsStack.Children.Clear();
            if (day != null)
            {
                foreach (var item in day.Lessons)
                {
                    if (item.IsCompleted) continue;
                    var shape = new Ellipse()
                    {
                        WidthRequest = 24,
                        HeightRequest = 24,
                        Margin = new Thickness(0, 0),
                        Fill = item.Subject?.Color ?? default
                    };

                    if (item.IsDuring)
                    {
                        shape.Stroke = Brush.White;
                        shape.StrokeThickness = 2;
                    }

                    LessonsStack.Children.Add(shape);
                }
            }
        }

        private void Settings_Clicked(object sender, EventArgs e)
        {
        }

        private void Menu_Clicked(object sender, EventArgs e)
        {
            Shell.Current.FlyoutIsPresented = true;
        }

        private void ReportListView_ItemSelected(object sender, SelectedItemChangedEventArgs e)
        {
            if (BindingContext is DiscoverViewModel vm && e.SelectedItem is Report report)
            {
                ReportListView.SelectedItem = null;
                vm.GoToReportDetail.Execute(report);
            }
        }
    }
}