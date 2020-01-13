% Takes two time series by name from the FRED database and checks for a
% linear relationship, returning a slope and rsq value.

% Parameters:
%  - connection: the MATLAB FRED connection to use to get all the data
%  - series1, series2: the tags of the FRED time series as strings
%  - rate1, rate2: Whether to recalculate the time series 1 and/or 2 as
%  growth rates or not (percentage change)
%  - timelag: The days after which to compare time series 2 to 1; for
%  instance, if the timelag is 10, we compare time series 2 shifted 10 days
%  forward to time series 1.
%  - ploton: Whether to show the plot showing series 1's values v. series
% 2's values.
%  - calculate: Whether to calculate and return correct values of the
%  linear fit (slope, rsq)

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
    
    % Pass info to rcheck to finish the analysis
    [slope, rsq] = rcheck(years1, years2, values1, values2, rate1, rate2, timelag, ploton, calculate);
end