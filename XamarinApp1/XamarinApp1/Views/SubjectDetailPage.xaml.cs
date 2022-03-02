using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using XamarinApp1.ViewModels;

using XF.Material.Forms.UI.Dialogs;

namespace XamarinApp1.Views;

public partial class SubjectDetailPage : ContentPage
{
    public SubjectDetailPage()
    {
        InitializeComponent();
    }

    private async void Rename_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is SubjectDetailViewModel viewModel)
        {
            await viewModel.RefreshTask;
            var name = await MaterialDialog.Instance.InputAsync(
                "教科の名前を変更", "教科の名前を入力", viewModel.Subject.Value.SubjectName);

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
}
