Changelog
=======

**v2.6a**
Original [program](https://github.com/paul-lysak/divider), created by Paul Lysak.

**v2.6b**
- Add additional package hierarchy level - *fem*, add subpackeges *geometry*, *apacker*, *common*
	* *geometry* absorb *Dot* and *Line* classes
	* *common* hold some project settings (e.g. program version)
- Start file-chooser dialog from current directory

**v2.7**
- Refactoring the *DPacker* code
- Add 'material' to *geometry.Dot*: modify *divider.mesh.NodeEditDialog* (change 'material' by GUI) and *divider.figure.DFig* (make 'material' save/load), modify *divider.figure.Node* for different color marking
- Fix 'out of boundary' error in *divider.figure.Contour* in method addToEnd 

**v2.7a**
- Suggest middle point for new node, when split a segment
