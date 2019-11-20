# Yield Curve Web Scraper
Downloads and parses all the yield curve data from the U.S. treasury website, putting the results in a csv file. By running the MATLAB application, you can also animate the yield curve over time.

## How to Use
1. *Viewing the data*: If you're just looking for a compilation of the yield curve data as a csv file, check out the `all_yield_data.csv` file. The first column is the dates the interest rates were recorded and the other columns are the interest rates for the bonds of each maturity. The various maturities are listed in the first row in months.
2. **Updating the data**: If you think the csv file (`all_yield_data.csv`) is not up to date, then run the Java program `YieldScraper.java`. This will download all the yield curve data from the [treasury yield curve website](https://www.treasury.gov/resource-center/data-chart-center/interest-rates/pages/textview.aspx?data=yield) and rewrite the `all_yield_data.csv` to match the most current data. Warning: The program takes a significant time to run (~30 seconds, depending on your computer and your Internet connection).
3. **Running the animation**: Running the MATLAB program `plotanimation.m` will launch an animation plotting the yield curve over time.

## Future Plans
* Adding program to check whether csv file is up-to-date without re-downloading all data.
* Creating and uploading a video (mp4?) of the yield curve animation for those who don't have MATLAB installed.
* Packaging the Java files into JAR executables. If possible, also make available as EXE.
* Add another program to detect inversions in the yield curve and quantify the extent of the inversion.
* If you have any more suggestions, just let me know!

## Acknowledgements
Thank you to the U.S. treasury for collecting all the yield curve data so regularly. Thank you also to *NPR*'s *Planet Money* podcast for introducing me to the yield curve; this project is dedicated to Cardiff Garcia, a co-host of the podcast who is known to especially love the yield-curve.
