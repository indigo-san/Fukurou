using System;

namespace XamarinApp1.Models
{
    public record Subject(string SubjectName);

    public record Teacher(string Name, Subject[] Subjects);

    public record Lesson(Teacher[] Teachers, Subject Subject, DateOnly Date, TimeOnly Start, TimeOnly End);
    
    public record Report(Subject Subject, DateOnly Date, string Name);

    public class Item
    {
        public string Id { get; set; }
        public string Text { get; set; }
        public string Description { get; set; }
    }
}