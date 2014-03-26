report.pdf: report.tex
	latexmk -pdf report.tex

report.tex: report.rst
	rst2latex.py report.rst > report.tex

clean: 
	cp report.rst /tmp && rm -f report.*
	cp /tmp/report.rst .
