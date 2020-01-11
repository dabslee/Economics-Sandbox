url = 'https://fred.stlouisfed.org/';
connection = fred(url);

[a, b] = rcheck_names(connection, "UNRATE", "ECIWAG", false, true, 0, true, true)
xlabel("Unemployment rate (%)");
ylabel("Wage rise rate (%)");