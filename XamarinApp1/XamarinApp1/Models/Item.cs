using Reactive.Bindings;

using System;

using Xamarin.Forms;

namespace XamarinApp1.Models;

public record Subject(string SubjectName, Guid Id, Color Color);

public record Teacher(string Name, Subject[] Subjects, Guid Id);

public record Lesson(Subject Subject, DateOnly Date, TimeOnly Start, TimeOnly End, Guid Id)
{
    public Teacher[] Teachers { get; init; } = Array.Empty<Teacher>();

    public bool IsCompleted
    {
        get
        {
            var now = TimeOnly.FromDateTime(DateTime.Now);
            var nowDateOnly = DateOnly.FromDateTime(DateTime.Now);
            return End < now && Date <= nowDateOnly;
        }
    }

    public bool IsDuring
    {
        get
        {
            var now = TimeOnly.FromDateTime(DateTime.Now);
            var nowDateOnly = DateOnly.FromDateTime(DateTime.Now);
            return Date == nowDateOnly &&
                Start < now &&
                now < End;
        }
    }
}

public enum ReportState
{
    Submitted,
    NotSubmitted
}

public record Report(Subject Subject, DateOnly Date, string Name, Guid Id, ReportState State)
{
    public bool IsSubmitted => State == ReportState.Submitted;

    public bool IsNotSubmitted => State == ReportState.NotSubmitted;

    public bool IsExpirationOfTerm => IsNotSubmitted && Date < DateOnly.FromDateTime(DateTime.Now);
}

public record SchoolDay(DateOnly Date, Guid Id)
{
    public string DateHeader
    {
        get
        {
            var nowDateOnly = DateOnly.FromDateTime(DateTime.Now);
            if (nowDateOnly == Date)
            {
                return "本日";
            }
            else if (nowDateOnly < Date)
            {
                return (Date.DayNumber - nowDateOnly.DayNumber).ToString();
            }
            else if (nowDateOnly > Date)
            {
                return (nowDateOnly.DayNumber - Date.DayNumber).ToString();
            }
            else
            {
                return "";
            }
        }
    }

    public string DateHeaderSuffix
    {
        get
        {
            var nowDateOnly = DateOnly.FromDateTime(DateTime.Now);
            if (nowDateOnly == Date)
            {
                return "";
            }
            else if (nowDateOnly < Date)
            {
                return "日後";
            }
            else if (nowDateOnly > Date)
            {
                return "日前";
            }
            else
            {
                return "";
            }
        }
    }

    public Lesson[] Lessons { get; init; } = Array.Empty<Lesson>();

    public Report[] Reports { get; init; } = Array.Empty<Report>();
}
