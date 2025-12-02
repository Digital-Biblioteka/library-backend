package nsu.library.tools;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;

public class MinioInit {
    public static void main(String[] args) throws Exception {
        String endpoint = getenvOr("MINIO_ENDPOINT", "http://localhost:9000");
        String access = getenvOr("MINIO_ROOT_USER", getenvOr("MINIO_ACCESS_KEY", "minioadmin"));
        String secret = getenvOr("MINIO_ROOT_PASSWORD", getenvOr("MINIO_SECRET_KEY", "minioadmin"));
        String raw = getenvOr("RAW_BUCKET", "raw");
        String cover = getenvOr("COVER_BUCKET", "cover");
        String parsed = getenvOr("PARSED_BUCKET", "parsed");
        String index = getenvOr("INDEX_BUCKET", "index");
        String snaps = getenvOr("ES_SNAPSHOTS_BUCKET", "es-snapshots");

        MinioClient mc = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(access, secret)
                .build();

        ensureBucket(mc, raw);
        ensureBucket(mc, parsed);
        ensureBucket(mc, index);
        ensureBucket(mc, snaps);
        ensureBucket(mc, cover);
    }

    private static void ensureBucket(MinioClient mc, String name) throws Exception {
        boolean exists = mc.bucketExists(BucketExistsArgs.builder().bucket(name).build());
        if (!exists) {
            mc.makeBucket(MakeBucketArgs.builder().bucket(name).build());
        }
    }

    private static String getenvOr(String k, String def) {
        String v = System.getenv(k);
        return v != null && !v.isEmpty() ? v : def;
        
    }
}
