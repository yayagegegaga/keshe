from pathlib import Path
from zipfile import ZipFile
from docx import Document
from docx.oxml.ns import qn

src = Path(r'C:\Users\31310\Desktop\哈威在读猫猫档案册-(1).docx')
out = Path(r'C:\Users\31310\Desktop\animal\.runtime\cat-docx-media')
out.mkdir(parents=True, exist_ok=True)
doc = Document(str(src))
rels = doc.part.rels
rows = doc.tables[0].rows
with ZipFile(src) as z:
    for i, row in enumerate(rows[1:], start=1):
        name = row.cells[0].text.strip()
        area = row.cells[2].text.strip()
        rids = []
        for drawing in row.cells[1]._tc.xpath('.//w:drawing'):
            for blip in drawing.xpath('.//a:blip'):
                rid = blip.get(qn('r:embed'))
                if rid:
                    rids.append(rid)
        image_names = []
        for n, rid in enumerate(rids, start=1):
            target = rels[rid].target_ref
            member = 'word/' + target.replace('\\', '/') if not target.startswith('word/') else target
            suffix = Path(target).suffix.lower() or '.png'
            filename = f'{i:02d}_{n:02d}_{name}{suffix}'
            dest = out / filename
            dest.write_bytes(z.read(member))
            image_names.append(filename)
        print(f'{i}\t{name}\t{area}\t{",".join(image_names)}')
