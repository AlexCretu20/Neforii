using System;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Drawing;
using System.Security.AccessControl;
using AppCore;

namespace ImageProcessingEntrypoint
{
    class Program
    {
        private static void Main(string[] args)
        {
            var filters = FilterLoader.LoadAll(PathConfig.FiltersDir);
            ImageProcessingService.ProcessImages(filters, PathConfig.InputDir, PathConfig.OutputDir,
                PathConfig.ImageExtensions); // aplica toate filtrele pe toate imaginile momentan
        }
    }
}