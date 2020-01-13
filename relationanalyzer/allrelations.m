% Goes through all the series listed in the `indicators.txt` file and
% records all the linear fit slopes and r-squared values in
% `allresults.csv`.

% set up arrays
allseries = table2array(readtable("indicators.txt"));
allresults = ["Series 1","Series 2","Slope","R^2"]; % series1, series2, slope, rsq

% establish connection to FRED
url = 'https://fred.stlouisfed.org/';
connection = fred(url);

% goes through all series combinations and checks for linear relationships
for i = 1:numel(allseries)
    for j = i+1:numel(allseries)
        series1 = allseries(i);
        series2 = allseries(j);
        disp("Analyzing "+series1+" and "+series2);
        
        [slope, rsq] = rcheck_names(connection, series1, series2, false, false, 0, false, true);
        allresults = [allresults; series1, series2, slope, rsq];
    end
end

% saves final allresults matrix as csv file
writetable(array2table(allresults),"allresults.csv");