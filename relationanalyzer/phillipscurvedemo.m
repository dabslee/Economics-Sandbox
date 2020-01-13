% A demonstration of the rcheck_names function that plots the rate of wage
% growth against the unemployment rate, showing the inverse relationship
% described by the Phillips Curve. 

% Establish connection
url = 'https://fred.stlouisfed.org/';
connection = fred(url);

% Run rcheck_names and plot
[a, b] = rcheck_names(connection, "UNRATE", "ECIWAG", false, true, 0, true, true)
xlabel("Unemployment rate (%)");
ylabel("Wage growth rate (%)");