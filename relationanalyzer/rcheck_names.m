% Takes two time series from the FRED database and checks for a linear
% relationship. Also offers whether to convert absolute value to growth
% rates. Also offers timelag (Number of days after which we match series2
% to series1). ploton is whether to plot the graph or not.

% Returns slope and Rsq value.

function [slope, rsq] = rcheck_names(connection, series1, series2, rate1, rate2, timelag, ploton, calculate)
    % Download all data
    downloaded = fetch(connection,series1);
    years1 = downloaded.Data(:,1);
    values1 = downloaded.Data(:,2);
    downloaded = fetch(connection,series2);
    years2 = downloaded.Data(:,1);
    values2 = downloaded.Data(:,2);

    % Do growth rate adjustments if necessary
    if rate1
        values1 = (values1(2:end)-values1(1:end-1))./values1(1:end-1)*100;
        years1 = years1(1:end-1);
    end
    if rate2
        values2 = (values2(2:end)-values2(1:end-1))./values2(1:end-1)*100;
        years2 = years2(1:end-1);
    end
    
    [slope, rsq] = rcheck(years1, years2, values1, values2, rate1, rate2, timelag, ploton, calculate);
end