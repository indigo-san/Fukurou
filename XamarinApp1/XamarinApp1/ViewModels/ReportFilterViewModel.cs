using Reactive.Bindings;

using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using XamarinApp1.Models;

namespace XamarinApp1.ViewModels;

public class ReportFilterViewModel : BaseViewModel
{
    public ReportFilterViewModel()
    {
        InitializeTask = Task.Run(async () =>
        {
            await foreach (var item in SubjectDataStore.GetItemsAsync().OrderBy(i => i.SubjectName))
            {
                Subjects.Add(item);
            }
            SelectedSubject.Value = Subjects.FirstOrDefault();
        });
    }

    public ObservableCollection<Subject> Subjects { get; } = new();

    public ReactivePropertySlim<bool> IsSubjectEnabled { get; } = new();
    
    public ReactivePropertySlim<Subject> SelectedSubject { get; } = new();

    public ReactivePropertySlim<int> ArchiveMode { get; } = new();

    public ReactivePropertySlim<bool> IsArchiveModeEnabled { get; } = new();

    public ReactivePropertySlim<int> ReportState { get; } = new();

    public ReactivePropertySlim<bool> IsMinDateEnabled { get; } = new();
    
    public ReactivePropertySlim<DateTime> MinDate { get; } = new(DateTime.MinValue);

    public ReactivePropertySlim<bool> IsMaxDateEnabled { get; } = new();

    public ReactivePropertySlim<DateTime> MaxDate { get; } = new(DateTime.MaxValue);

    public Task InitializeTask { get; }
}
