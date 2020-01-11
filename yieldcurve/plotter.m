% Reads the data from the yield data csv created by YieldScraper.java. Can
% perform two functions with the data:
%  - If showAnimation=true, shows an animation of the yield curve over time
%  - If showDerivatives=true, shows graph of the mean derivative of the
%    yield curve over time at the end of program.

showAnimation = false;
animationSpeed = 10;
showDerivatives = true;
smoothing = 10; % smoother, less accurate graph for derivatives graph

data = readmatrix("all_yield_data.csv");
dates = readtable("all_yield_data.csv");
dates = table2array(dates(2:end,1));

% An array of the mean derivatives of the yield curve at each year.
Ys = [];

% Reads the csv and animates the yield curve at every date. Meanwhile, also
% keeps track of the mean derivatives at each date and stores the values in
% Ys.
for i = 2:animationSpeed:length(data)
    maturities = data(1,2:end);
    interestrates = data(i,2:end);
    
    Ys = [Ys, inversionY(maturities,interestrates)];
    
    % Plot the yield curve at this date (animation)
    if (showAnimation)
        figure(1);
        plot(maturities,interestrates,'LineWidth',1);
        title("Yield curve on " + dates(i));
        xlabel("Maturity (months)");
        ylabel("Interest rate (%)");
        xlim([0,360]);
        ylim([0,10]);
        set(gcf,'color','w');
        pause(0.001);
    end
end

% Plot the final yield curve mean derivatives over date graph as a moving
% average
if (showDerivatives)
    % Calculating the moving average
    % A larger `lag` will lead to a smoother but less accurate graph.
    lag = smoothing;
    thesum = Ys(lag:end);
    for i = 1:lag-1
        thesum = thesum + Ys(lag-i:end-i);
    end
    ma = thesum/lag;
    
    % Actually plotting the graph
    figure(2);
    plot(1:length(ma),ma);
    xticks(1:length(ma)/30:length(ma));
    xticklabels(1990:2019);
    xtickangle(90);
    yline(0);
    title("Yield Curve Mean Derivative");
    xlabel("Date (yr)");
    ylabel("Mean Derivative (%/mo)");
    set(gcf,'color','w');
end

% Returns the mean derivative for the yield curve given the array of
% maturities and their correspondinginterest rates.
function Y = inversionY(t, i)
    indices = find(~isnan(i));
    i = i(indices);
    t = t(indices);
    try
        Y = (i(end)-i(1)) ./ (t(end)-t(1));
    catch
        Y = NaN;
    end
end

% Returns whether there is a point where the interest rate decreases
% instead of increasing.
function inverts = inversionCheck(t, i)
    inverts = sum(i(2:end) < i(1:end-1)) > 0;
end