% Reads the data from the yield data csv created by YieldScraper.java and
%   uses it to create an animation of the yield curve over time.

data = readmatrix("all_yield_data.csv");
dates = readtable("all_yield_data.csv");
dates = table2array(dates(2:end,1));

for i = 2:length(data)
    plot(data(1,2:end),data(i,2:end),'LineWidth',1);
    title("Yield curve on " + dates(i));
    xlabel("Maturity (months)");
    ylabel("Interest rate (%)");
    xlim([0,360]);
    ylim([0,10]);
    set(gcf,'color','w');
    pause(0.001);
end