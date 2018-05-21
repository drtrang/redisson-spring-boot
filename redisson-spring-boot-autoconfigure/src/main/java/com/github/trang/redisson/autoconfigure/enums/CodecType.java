package com.github.trang.redisson.autoconfigure.enums;

import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.*;

/**
 * 序列化方式
 *
 * @author trang
 */
public enum CodecType {

    /**
     * Jackson JSON 编码 默认编码
     */
    JACKSON {
        @Override
        public Codec getInstance() {
            return new JsonJacksonCodec();
        }
    },

    /**
     * 另一个进制的 JSON 编码
     */
    SMILE {
        @Override
        public Codec getInstance() {
            return new SmileJacksonCodec();
        }
    },

    /**
     * 又一个二进制的 JSON 编码
     */
    CBOR {
        @Override
        public Codec getInstance() {
            return new CborJacksonCodec();
        }
    },

    /**
     * 再来一个二进制的 JSON 编码
     */
    MSG_PACK {
        @Override
        public Codec getInstance() {
            return new MsgPackJacksonCodec();
        }
    },

    /**
     * Kryo 二进制对象序列化编码
     */
    KRYO {
        @Override
        public Codec getInstance() {
            return new KryoCodec();
        }
    },

    /**
     * JDK 序列化编码
     */
    JDK {
        @Override
        public Codec getInstance() {
            return new SerializationCodec();
        }
    },

    /**
     * Fst 二进制对象序列化编码
     */
    FST {
        @Override
        public Codec getInstance() {
            return new FstCodec();
        }
    },

    /**
     * 压缩型序列化对象编码
     */
    LZ4 {
        @Override
        public Codec getInstance() {
            return new LZ4Codec();
        }
    },

    /**
     * 另一个压缩型序列化对象编码
     */
    SNAPPY {
        @Override
        public Codec getInstance() {
            return new SnappyCodec();
        }
    },

    /**
     * 纯字符串编码（无转换）
     */
    STRING {
        @Override
        public Codec getInstance() {
            return new StringCodec();
        }
    },

    /**
     * 字节数组编码
     */
    BYTE_ARRAY {
        @Override
        public Codec getInstance() {
            return new ByteArrayCodec();
        }
    };

    public abstract Codec getInstance();

}