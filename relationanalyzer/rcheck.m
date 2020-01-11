% Takes two time series' values and dates and checks for a linear
% relationship, returning a slope and rsq value.

function [slope, rsq] = rcheck(years1, years2, values1, values2, rate1, rate2, timelag, ploton, calculate)
    % Match up date values
    % Match up starting dates
    indices = find(years1 >= years2(1));
    years1 = years1(indices);
    values1 = values1(indices);
    indices = find(years2 >= years1(1));
    years2 = years2(indices);
    values2 = values2(indices);
    % Adjust for time lag
    indices = find(years1 <= years1(end) - timelag);
    years1 = years1(indices);
    values1 = values1(indices);
    indices = find(years2 >= years2(1) + timelag);
    years2 = years2(indices);
    values2 = values2(indices);
    % Match up observation frequency
    if numel(years1) > numel(years2)
        nfactor = floor(numel(years1)/numel(years2));
        newdata2 = NaN(nfactor*numel(values2),1);
        for i = 1:numel(values2)
            for j = 0:nfactor-1
                newdata2(nfactor*i-j) = values2(i);
            end
        end
        values2 = newdata2;
    elseif numel(years2) > numel(years1)
        nfactor = floor(numel(years2)/numel(years1));
        newdata1 = NaN(nfactor*numel(values1),1);
        for i = 1:numel(values1)
            for j = 0:nfactor-1
                newdata1(nfactor*i-j) = values1(i);
            end
        end
        values1 = newdata1;
    end

    % one last length check
    finlen = min(numel(values1),numel(values2));
    values1 = values1(1:finlen);
    values2 = values2(1:finlen);

    % plot and do linear regression
    slope = 0;
    rsq = 0;
    if (calculate)
        tb = table(values1,values2);
        lm = fitlm(tb,'linear');
        coeffs = lm.Coefficients.Estimate;
        slope = num2str(coeffs(2));
        rsq = num2str(lm.Rsquared.Ordinary);
    end
    
    if (ploton)
        plot(values1,values2,'.');
        hold on

        allxs = min(values1):(max(values1)-min(values1))/1000:max(values1);
        if (calculate)
            plot(allxs,coeffs(1)+coeffs(2)*allxs);
        end
        if rate1
            xlabel("x Growth rate (%)");
        else
            xlabel("x");
        end
        if rate2
            ylabel("y Growth rate (%)");
        else
            ylabel("y");
        end
        hold off
    end
end