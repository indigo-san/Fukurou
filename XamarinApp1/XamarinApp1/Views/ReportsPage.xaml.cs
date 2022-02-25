using System;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.ViewModels;

namespace XamarinApp1.Views;

public partial class ReportsPage : ContentPage
{
    public ReportsPage()
    {
        InitializeComponent();
    }

    private void ScrollToToday_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is not ReportsViewModel vm) return;
        var today = DateOnly.FromDateTime(DateTime.Now);

        for (int i = 0; i < vm.Items.Count - 1; i++)
        {
            var current = vm.Items[i];
            var next = vm.Items[i + 1];

            if (current.Date <= today && today <= next.Date)
            {
                CollectionView1.ScrollTo(0, i + 1, ScrollToPosition.Start);
                return;
            }
        }
    }
}
