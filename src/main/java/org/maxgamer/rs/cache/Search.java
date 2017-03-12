package org.maxgamer.rs.cache;

import org.maxgamer.rs.cache.reference.ChildReference;
import org.maxgamer.rs.cache.reference.Reference;
import org.maxgamer.rs.cache.reference.ReferenceTable;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Search {
    private static Cache cache;

    public static void main(String[] args) throws IOException {
        cache = new Cache(new File("cache"));
        search("points", IDX.INTERFACE_SCRIPTS);
    }

    public static void search(String term, int idx) throws IOException {
        ReferenceTable table = cache.getReferenceTable(idx);
        for (Reference ref : table.getReferences()) {
            if (ref.getChildCount() > 1) {
                // Archive
                Archive archive = cache.getArchive(idx, ref.getId());
                for (ChildReference child : ref.getChildren()) {
                    ByteBuffer bb = archive.get(child.getId());
                    search(term, "idx " + idx + ", archive " + ref.getId() + ", child " + child.getId(), bb);
                }
            } else {
                // Standard file
                ByteBuffer bb = cache.getFile(idx, ref.getId()).getData();
                search(term, "idx " + idx + ", file " + ref.getId(), bb);
            }
        }
        System.out.println("Finished searching IDX " + idx);
    }

    public static void search(String term, String name, ByteBuffer bb) {
        StringBuilder sb = new StringBuilder(bb.remaining());
        while (bb.hasRemaining())
            sb.append((char) bb.get());
        String s = sb.toString();
        int index = s.indexOf(term);
        if (index >= 0) {
            System.out.println(name + " contained search at index " + index + "!");
            System.out.println(s);
        }
    }
}