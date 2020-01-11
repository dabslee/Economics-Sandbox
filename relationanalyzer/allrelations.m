% Goes through all the series offered by FRED and checks for the
% relationships with maximum strength.

% This version is streamlined without any rate checking or timelag checking

allseries = table2array(readtable("indicators.txt"));
allresults = ["Series 1","Series 2","Slope","R^2"]; % series1, series2, slope, rsq

url = 'https://fred.stlouisfed.org/';
connection = fred(url);

for i = 1:numel(allseries)
    for j = i+1:numel(allseries)
        series1 = allseries(i);
        series2 = allseries(j);
        disp("Analyzing "+series1+" and "+series2);
        
        [slope, rsq] = rcheck_names(connection, series1, series2, false, false, 0, false, true);
        allresults = [allresults; series1, series2, slope, rsq];
    end
end

writetable(array2table(allresults),"allresults.csv");