using Reactive.Bindings;

using System;
using System.Diagnostics;

using Xamarin.Forms;

using XamarinApp1.Models;

namespace XamarinApp1.ViewModels;

[QueryProperty(nameof(ItemId), nameof(ItemId))]
public class ReportDetailViewModel : BaseViewModel
{
    private string itemId;

    public ReportDetailViewModel()
    {
        MarkAsSubmitted.Subscribe(async () =>
        {
            if (Report.Value != null)
            {
                Report.Value = Report.Value with
                {
                    State = ReportState.Submitted
                };
                await ReportDataStore.UpdateItemAsync(Report.Value);
            }
        });

        MarkAsNotSubmitted.Subscribe(async () =>
        {
            if (Report.Value != null)
            {
                Report.Value = Report.Value with
                {
                    State = ReportState.NotSubmitted
                };
                await ReportDataStore.UpdateItemAsync(Report.Value);
            }
        });

        Refresh.Subscribe(() => LoadItemId(itemId));
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

    public ReactiveCommand MarkAsSubmitted { get; } = new();

    public ReactiveCommand MarkAsNotSubmitted { get; } = new();

    public ReactivePropertySlim<Report> Report { get; } = new();

    public async void LoadItemId(string itemId)
    {
        IsBusy = true;
        try
        {
            Report.Value = await ReportDataStore.GetItemAsync(Guid.Parse(itemId));
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
