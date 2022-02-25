
using Reactive.Bindings;
using Reactive.Bindings.Extensions;

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
[QueryProperty(nameof(NewAction), nameof(NewAction))]
public class ReportDetailViewModel : BaseViewModel
{
    private string itemId;
    private bool newAction;

    public ReportDetailViewModel()
    {
        Report.Subscribe(r => itemId = r?.Id.ToString() ?? null);
        SelectedSubject = Report.Select(i => i?.Subject).ToReactiveProperty();
        SelectedDate = Report.Select(i => (i?.Date.ToDateTime(default)) ?? DateTime.Now).ToReactiveProperty();
        Name = Report.Select(i => i?.Name ?? string.Empty).ToReactiveProperty();

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

        var canSave = SelectedSubject.CombineLatest(Name).Select(t => t.First != null && !string.IsNullOrWhiteSpace(t.Second));
        Save = new ReactiveCommand(canSave);
        Save.Subscribe(async () =>
        {
            if (Report.Value != null)
            {
                var oldValue = Report.Value;
                Report.Value = Report.Value with
                {
                    Subject = SelectedSubject.Value,
                    Date = DateOnly.FromDateTime(SelectedDate.Value),
                    Name = Name.Value,
                };

                await ReportDataStore.UpdateItemAsync(Report.Value);

                // 元に戻す
                if (await MaterialDialog.Instance.SnackbarAsync("変更が保存されました", "元に戻す"))
                {
                    Report.Value = oldValue;
                    await ReportDataStore.UpdateItemAsync(Report.Value);
                }
            }
            else if (NewAction)
            {
                Report.Value = new Report(
                    SelectedSubject.Value,
                    DateOnly.FromDateTime(SelectedDate.Value),
                    Name.Value,
                    Guid.NewGuid(),
                    ReportState.NotSubmitted);

                await ReportDataStore.AddItemAsync(Report.Value);
                NewAction = false;

                // 元に戻す
                if (await MaterialDialog.Instance.SnackbarAsync("レポートが追加されました", "元に戻す"))
                {
                    await ReportDataStore.DeleteItemAsync(Report.Value.Id);
                    NewAction = true;
                }
            }
        });

        Delete = new ReactiveCommand(this.ObserveProperty(o => o.NewAction).Select(b => !b));
        Delete.Subscribe(async () =>
        {
            if (Report.Value != null)
            {
                var oldValue = Report.Value;
                await ReportDataStore.DeleteItemAsync(Report.Value.Id);
                Report.Value = null;
                await Shell.Current.GoToAsync("..");

                // 元に戻す
                if (await MaterialDialog.Instance.SnackbarAsync("アイテムが削除されました", "元に戻す"))
                {
                    await ReportDataStore.AddItemAsync(oldValue);
                }
            }
        });

        Refresh.Subscribe(() => LoadItemId(itemId));
        Task.Run(async () =>
        {
            IsBusy = true;

            try
            {
                Subjects.Value = (await SubjectDataStore.GetItemsAsync(true)).ToArray();
                SelectedSubject.Value ??= Subjects.Value.FirstOrDefault();
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

    public bool NewAction
    {
        get => newAction;
        set => SetProperty(ref newAction, value);
    }

    public ReactiveCommand Refresh { get; } = new();

    public ReactiveCommand Save { get; }

    public ReactiveCommand Delete { get; }

    public ReactiveProperty<Subject> SelectedSubject { get; }

    public ReactiveProperty<DateTime> SelectedDate { get; }

    public ReactiveProperty<string> Name { get; }

    public ReactivePropertySlim<Subject[]> Subjects { get; } = new();

    public ReactiveCommand MarkAsSubmitted { get; } = new();

    public ReactiveCommand MarkAsNotSubmitted { get; } = new();

    public ReactivePropertySlim<Report> Report { get; } = new();

    public async void LoadItemId(string itemId)
    {
        try
        {
            IsBusy = true;
            if (NewAction || !Guid.TryParse(itemId, out var id))
                return;

            Report.Value = await ReportDataStore.GetItemAsync(id);
            Report.ForceNotify();
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
